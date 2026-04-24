package madp.appdeployment.domain.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import madp.appdeployment.domain.application.support.ProjectResourceLockManager;
import madp.appdeployment.domain.domain.entity.AppDeploymentEntity;
import madp.appdeployment.domain.domain.entity.GithubAllowedRepoEntity;
import madp.appdeployment.domain.domain.enums.AppDeploymentStatus;
import madp.appdeployment.domain.domain.repository.AppDeploymentRepository;
import madp.appdeployment.domain.domain.repository.GithubAllowedRepoRepository;
import madp.appdeployment.domain.domain.repository.dto.ProjectResourceUsageSumDto;
import madp.appdeployment.domain.domain.vo.ResourceInfo;
import madp.appdeployment.domain.domain.repository.AppDeploymentTagRepository;
import madp.appdeployment.domain.exception.AppDeploymentNotFoundException;
import madp.appdeployment.domain.exception.AppDeploymentVersionNotFoundException;
import madp.appdeployment.domain.exception.GithubAllowedRepoNotFoundException;
import madp.appdeployment.domain.exception.InvalidAppDeploymentException;
import madp.appdeployment.domain.exception.InvalidResourceInfoException;
import madp.appdeployment.domain.exception.ProjectAccessDeniedException;
import madp.appdeployment.domain.infrastructure.client.ProjectClient;
import madp.appdeployment.domain.infrastructure.client.ResourceClient;
import madp.appdeployment.domain.infrastructure.client.request.AppRevisionRequestDto;
import madp.appdeployment.domain.infrastructure.client.request.UpdateAppImageRequestDto;
import madp.appdeployment.domain.infrastructure.client.response.AppDeploymentResourceStatusResponseDto;
import madp.appdeployment.domain.infrastructure.client.response.PodLogsResponseDto;
import madp.appdeployment.domain.infrastructure.client.response.ProjectResourceLimitResponseDto;
import madp.appdeployment.domain.presentation.dto.request.CreateAppDeploymentRequestDto;
import madp.appdeployment.domain.presentation.dto.request.UpdateGithubInfoRequestDto;
import madp.appdeployment.domain.presentation.dto.response.AppBuildLogDetailResponseDto;
import madp.appdeployment.domain.presentation.dto.response.AppBuildLogListResponseDto;
import madp.appdeployment.domain.presentation.dto.response.AppDeploymentInfoResponseDto;
import madp.appdeployment.domain.presentation.dto.response.AppDeploymentListResponseDto;
import madp.appdeployment.domain.presentation.dto.response.AppDeploymentStatusResponseDto;
import madp.appdeployment.domain.presentation.dto.response.AppDeploymentSummaryResponseDto;
import madp.appdeployment.domain.presentation.dto.response.AppResourceStatusResponseDto;
import madp.appdeployment.global.exception.service.ExternalServiceUnavailableException;
import madp.appdeployment.global.infrastructure.feign.exception.FeignClientNotFoundException;
import madp.appdeployment.global.presentation.dto.response.ApiResponseDto;
import madp.appdeployment.domain.application.event.GithubRepoLinkedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppDeploymentService {
    private static final int MI_PER_GB = 1024;
    private final AppDeploymentRepository appDeploymentRepository;
    private final AppDeploymentTagRepository appDeploymentTagRepository;
    private final GithubAllowedRepoRepository githubAllowedRepoRepository;
    private final ProjectClient projectClient;
    private final ResourceClient resourceClient;
    private final ProjectResourceLockManager projectResourceLockManager;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Long createAppDeployment(CreateAppDeploymentRequestDto createAppDeploymentRequestDto) {
        log.info("[createAppDeployment] 요청 - projectId={}, name={}, cpu={}, memory={}, disk={}",
                createAppDeploymentRequestDto.projectId(),
                createAppDeploymentRequestDto.name(),
                createAppDeploymentRequestDto.cpu(),
                createAppDeploymentRequestDto.memory(),
                createAppDeploymentRequestDto.disk());

        return projectResourceLockManager.executeWithLock(createAppDeploymentRequestDto.projectId(), () -> {
            if(!projectClient.getProjectOwner(createAppDeploymentRequestDto.projectId()).data().status()) {
                log.warn("[createAppDeployment] 프로젝트 오너 권한 없음 - projectId={}", createAppDeploymentRequestDto.projectId());
                throw new ProjectAccessDeniedException();
            }

            ResourceInfo resourceInfo = ResourceInfo.builder()
                    .cpu(createAppDeploymentRequestDto.cpu())
                    .disk(createAppDeploymentRequestDto.disk())
                    .memory(createAppDeploymentRequestDto.memory())
                    .build();
            validateProjectResourceLimitForCreate(createAppDeploymentRequestDto.projectId(), resourceInfo);

            AppDeploymentEntity appDeploymentEntity = AppDeploymentEntity.builder()
                    .name(createAppDeploymentRequestDto.name())
                    .projectId(createAppDeploymentRequestDto.projectId())
                    .resourceInfo(resourceInfo)
                    .build();

            Long savedId = appDeploymentRepository.save(appDeploymentEntity).getId();
            log.info("[createAppDeployment] 완료 - appDeploymentId={}", savedId);
            return savedId;
        });
    }

    @Transactional
    public void deleteAppDeployment(Long appDeploymentId) {
        log.info("[deleteAppDeployment] 요청 - appDeploymentId={}", appDeploymentId);

        AppDeploymentEntity appDeploymentEntity = appDeploymentRepository.findById(appDeploymentId)
                .orElseThrow(AppDeploymentNotFoundException::new);

        log.info("[deleteAppDeployment] 앱 조회 완료 - appDeploymentId={}, projectId={}, name={}",
                appDeploymentId, appDeploymentEntity.getProjectId(), appDeploymentEntity.getName());

        // 프로젝트 오너인지 확인하도록 변경 - 현재는 프로젝트 멤버인지 판별하는 로직임
        if(!projectClient.getProjectOwner(appDeploymentEntity.getProjectId()).data().status()) {
            log.warn("[deleteAppDeployment] 프로젝트 오너 권한 없음 - projectId={}", appDeploymentEntity.getProjectId());
            throw new ProjectAccessDeniedException();
        }

        // AppDeployment 삭제 시, 관련된 리소스(jenkins) 해제
        deleteResourceSafely(appDeploymentEntity);

        // GitHub Repository 연결 해제 (GithubAllowedRepoEntity는 시스템 엔티티이므로 삭제하지 않음)
        appDeploymentEntity.disconnectGithubRepository();
        log.info("[deleteAppDeployment] GitHub Repository 연결 해제 완료 - appDeploymentId={}", appDeploymentId);

        appDeploymentRepository.delete(appDeploymentEntity);
        log.info("[deleteAppDeployment] 완료 - appDeploymentId={}", appDeploymentId);
    }

    @Transactional
    public void deleteAppDeploymentListByProjectId(Long projectId) {
        String projectIdValue = String.valueOf(projectId);
        log.info("[deleteAppDeploymentListByProjectId] 요청 - projectId={}", projectIdValue);

        List<AppDeploymentEntity> appDeployments = appDeploymentRepository.findAllByProjectId(projectIdValue);
        log.info("[deleteAppDeploymentListByProjectId] 조회된 앱 수 - projectId={}, count={}", projectIdValue, appDeployments.size());

        for (AppDeploymentEntity appDeployment : appDeployments) {
            deleteResourceSafely(appDeployment);
        }

        // GitHub Repository 연결 일괄 해제 (GithubAllowedRepoEntity는 시스템 엔티티이므로 삭제하지 않음)
        appDeploymentRepository.disconnectGithubRepositoriesByProjectId(projectIdValue);
        log.info("[deleteAppDeploymentListByProjectId] GitHub Repository 연결 해제 완료 - projectId={}", projectIdValue);

        appDeploymentRepository.deleteAll(appDeployments);
        log.info("[deleteAppDeploymentListByProjectId] 완료 - projectId={}, deletedCount={}", projectIdValue, appDeployments.size());
    }

    private void deleteResourceSafely(AppDeploymentEntity appDeployment) {
        if (appDeployment.isPending()) {
            log.info("[deleteResourceSafely] PENDING 상태 앱 건너뛰기 - projectId={}, name={}",
                    appDeployment.getProjectId(), appDeployment.getName());
            return;
        }

        try {
            resourceClient.deleteAppDeployment(appDeployment.getProjectId(), appDeployment.getName());
            log.info("[deleteResourceSafely] 리소스 삭제 요청 완료 - projectId={}, name={}",
                    appDeployment.getProjectId(), appDeployment.getName());
        } catch (FeignClientNotFoundException e) {
            log.warn("[deleteResourceSafely] 리소스를 찾을 수 없음 (이미 삭제되었을 수 있음) - projectId={}, name={}",
                    appDeployment.getProjectId(), appDeployment.getName());
        } catch (Exception e) {
            log.error("[deleteResourceSafely] 리소스 삭제 중 오류 발생 - projectId={}, name={}",
                    appDeployment.getProjectId(), appDeployment.getName(), e);
        }
    }

    @Transactional
    public void updateAppDeploymentResourceInfo(Long appDeploymentId, ResourceInfo resourceInfo) {
        log.info("[updateAppDeploymentResourceInfo] 요청 - appDeploymentId={}, cpu={}, memory={}, disk={}",
                appDeploymentId, resourceInfo.getCpu(), resourceInfo.getMemory(), resourceInfo.getDisk());

        String projectId = appDeploymentRepository.findProjectIdById(appDeploymentId)
                .orElseThrow(AppDeploymentNotFoundException::new);

        projectResourceLockManager.executeWithLock(projectId, () -> {
            AppDeploymentEntity appDeploymentEntity = appDeploymentRepository.findById(appDeploymentId)
                    .orElseThrow(AppDeploymentNotFoundException::new);

            if(!projectClient.getProjectOwner(appDeploymentEntity.getProjectId()).data().status()) {
                log.warn("[updateAppDeploymentResourceInfo] 프로젝트 오너 권한 없음 - projectId={}", appDeploymentEntity.getProjectId());
                throw new ProjectAccessDeniedException();
            }

            validateProjectResourceLimitForUpdate(appDeploymentEntity, resourceInfo);

            resourceClient.reviseApp(
                    new AppRevisionRequestDto(
                            appDeploymentId.toString(),
                            toMilliCpu(resourceInfo.getCpu()),
                            toMi(resourceInfo.getMemory()),
                            toMi(resourceInfo.getDisk())
                    )
            );

            appDeploymentEntity.updateResourceInfo(resourceInfo);
            log.info("[updateAppDeploymentResourceInfo] 완료 - appDeploymentId={}", appDeploymentId);
        });
    }

    private void validateProjectResourceLimitForCreate(String projectId, ResourceInfo targetResourceInfo) {
        ProjectResourceUsageSumDto currentUsage = appDeploymentRepository.sumResourceUsageByProjectId(projectId);
        validateProjectResourceLimit(projectId, targetResourceInfo, currentUsage);
    }

    private void validateProjectResourceLimitForUpdate(AppDeploymentEntity appDeploymentEntity, ResourceInfo targetResourceInfo) {
        ProjectResourceUsageSumDto currentUsage = appDeploymentRepository.sumResourceUsageByProjectIdExcludingAppId(
                appDeploymentEntity.getProjectId(),
                appDeploymentEntity.getId()
        );
        validateProjectResourceLimit(appDeploymentEntity.getProjectId(), targetResourceInfo, currentUsage);
    }

    private void validateProjectResourceLimit(
            String projectId,
            ResourceInfo targetResourceInfo,
            ProjectResourceUsageSumDto currentUsage
    ) {
        ProjectResourceLimitResponseDto projectResourceLimit = projectClient.getProjectResourceLimit(projectId).data();
        double totalCpu = currentUsage.totalCpu().doubleValue() + targetResourceInfo.getCpu();
        double totalMemory = currentUsage.totalMemory().doubleValue() + targetResourceInfo.getMemory();
        double totalDisk = currentUsage.totalDisk().doubleValue() + targetResourceInfo.getDisk();

        if (totalCpu > projectResourceLimit.maxCpu()) {
            throw new InvalidResourceInfoException("프로젝트 최대 CPU 한도를 초과했습니다.");
        }
        if (totalMemory > projectResourceLimit.maxMemory()) {
            throw new InvalidResourceInfoException("프로젝트 최대 메모리 한도를 초과했습니다.");
        }
        if (totalDisk > projectResourceLimit.maxDisk()) {
            throw new InvalidResourceInfoException("프로젝트 최대 디스크 한도를 초과했습니다.");
        }
    }

    @Transactional
    public void updateGithubInfo(UpdateGithubInfoRequestDto updateGithubInfoRequestDto) {
        log.info("[updateGithubInfo] 요청 - appDeploymentId={}, owner={}, repository={}, branch={}",
                updateGithubInfoRequestDto.appDeploymentId(),
                updateGithubInfoRequestDto.owner(),
                updateGithubInfoRequestDto.repository(),
                updateGithubInfoRequestDto.branch());

        AppDeploymentEntity appDeploymentEntity = appDeploymentRepository.findById(updateGithubInfoRequestDto.appDeploymentId())
                .orElseThrow(AppDeploymentNotFoundException::new);

        if(!projectClient.getProjectOwner(appDeploymentEntity.getProjectId()).data().status()) {
            log.warn("[updateGithubInfo] 프로젝트 오너 권한 없음 - projectId={}", appDeploymentEntity.getProjectId());
            throw new ProjectAccessDeniedException();
        }

        String repositoryFullName = updateGithubInfoRequestDto.owner() + "/" + updateGithubInfoRequestDto.repository();
        GithubAllowedRepoEntity githubAllowedRepoEntity = githubAllowedRepoRepository.findByRepositoryFullName(repositoryFullName).orElseThrow(GithubAllowedRepoNotFoundException::new);

        if (appDeploymentRepository.existsByGithubRepositoryIdExcludingAppId(githubAllowedRepoEntity.getRepositoryId(), updateGithubInfoRequestDto.appDeploymentId())) {
            throw new InvalidAppDeploymentException("이미 다른 앱에 연결된 GitHub Repository입니다.");
        }

        appDeploymentEntity.uploadGithubInfo(updateGithubInfoRequestDto.branch(), githubAllowedRepoEntity);
        log.info("[updateGithubInfo] 완료 - appDeploymentId={}, repositoryFullName={}", updateGithubInfoRequestDto.appDeploymentId(), repositoryFullName);

        // Jenkins 빌드 트리거를 위해 이벤트 발행
        eventPublisher.publishEvent(new GithubRepoLinkedEvent(appDeploymentEntity));
        log.info("[updateGithubInfo] GithubRepoLinkedEvent 발행 완료 - appDeploymentId={}", appDeploymentEntity.getId());
    }

    @Transactional(readOnly = true)
    public List<AppDeploymentStatusResponseDto> getAppDeploymentsByProjectId(String projectId) {
        log.info("[getAppDeploymentsByProjectId] 요청 - projectId={}", projectId);

        if(!projectClient.getProjectAvailable(projectId).data().status()) {
            log.warn("[getAppDeploymentsByProjectId] 프로젝트 접근 권한 없음 - projectId={}", projectId);
            throw new ProjectAccessDeniedException();
        }

        List<AppDeploymentEntity> appDeploymentEntities = appDeploymentRepository.findAllByProjectId(projectId);
        List<String> names = appDeploymentEntities.stream().map(AppDeploymentEntity::getName).toList();
        log.info("[getAppDeploymentsByProjectId] 조회된 앱 목록 - projectId={}, appNames={}", projectId, names);

        ApiResponseDto<List<AppDeploymentResourceStatusResponseDto.AppResourceDto>> appDeploymentResourceStatusResponseDto =
                resourceClient.getAppDeploymentResourceStatus(projectId, names);
        Map<String, AppDeploymentEntity> appDeploymentEntityMap = appDeploymentEntities.stream()
                .collect(Collectors.toMap(AppDeploymentEntity::getName, Function.identity()));

        List<AppDeploymentStatusResponseDto> result = appDeploymentResourceStatusResponseDto.data().stream().map(
                (appResourceDto) -> {
                    AppDeploymentEntity appDeployment = appDeploymentEntityMap.get(appResourceDto.appId());
                    return AppDeploymentStatusResponseDto.builder()
                            .appId(appResourceDto.appId())
                            .cpuUsagePercentage(appResourceDto.cpu().percentage())
                            .memoryUsagePercentage(appResourceDto.memory().percentage())
                            .port(appDeployment.getPort())
                            .podCount(appResourceDto.instance().used())
                            .build();
                }
        ).toList();

        log.info("[getAppDeploymentsByProjectId] 완료 - projectId={}, resultCount={}", projectId, result.size());
        return result;
    }

    @Transactional(readOnly = true)
    public List<AppDeploymentListResponseDto> getAppDeploymentListByProjectId(Long projectId) {
        if (!projectClient.getProjectAvailable(String.valueOf(projectId)).data().status()) {
            throw new ProjectAccessDeniedException();
        }

        List<AppDeploymentEntity> apps = appDeploymentRepository.findAllByProjectId(String.valueOf(projectId));
        List<String> names = apps.stream().map(AppDeploymentEntity::getName).toList();

        Map<String, AppDeploymentResourceStatusResponseDto.AppResourceDto> resourceMap =
                resourceClient.getAppDeploymentResourceStatus(String.valueOf(projectId), names).data().stream()
                        .collect(Collectors.toMap(AppDeploymentResourceStatusResponseDto.AppResourceDto::appId, Function.identity()));

        return apps.stream().map(app -> {
            AppDeploymentResourceStatusResponseDto.AppResourceDto resource = resourceMap.get(app.getName());
            return AppDeploymentListResponseDto.builder()
                    .id(app.getId())
                    .name(app.getName())
                    .podCount(resource != null ? resource.instance().used() : 0)
                    .exposedPort(app.getPort())
                    .cpuUsagePercent(resource != null ? resource.cpu().percentage().doubleValue() : 0.0)
                    .ramUsagePercent(resource != null ? resource.memory().percentage().doubleValue() : 0.0)
                    .healthStatus(app.getStatus().name())
                    .build();
        }).toList();
    }

    @Transactional(readOnly = true)
    public List<AppDeploymentSummaryResponseDto> getAppDeploymentSummary(List<Long> projectIds) {
        return projectIds.stream().map(projectId -> {
            List<AppDeploymentEntity> apps = appDeploymentRepository.findAllByProjectId(String.valueOf(projectId));

            int running = (int) apps.stream()
                    .filter(app -> app.getStatus() == AppDeploymentStatus.RUNNING)
                    .count();
            int warning = apps.size() - running;
            String state = warning == 0 ? "RUNNING" : "STOPPED";

            return AppDeploymentSummaryResponseDto.builder()
                    .projectId(projectId)
                    .running(running)
                    .warning(warning)
                    .state(state)
                    .build();
        }).toList();
    }

    @Transactional(readOnly = true)
    public String getLogs(String projectId, String appName) {
        log.info("[getLogs] 요청 - projectId={}, appName={}", projectId, appName);

        if(!projectClient.getProjectAvailable(projectId).data().status()) {
            log.warn("[getLogs] 프로젝트 접근 권한 없음 - projectId={}", projectId);
            throw new ProjectAccessDeniedException();
        }
        log.info("[getLogs] project 요청 성공");

        ApiResponseDto<PodLogsResponseDto.LogDataDto> podLogsResponseDto = resourceClient.getPodLogs(projectId, appName);

        log.info("[getLogs] resource 요청 성공");

        String logs = podLogsResponseDto.data().podLogs().getFirst().logs();
        log.info("[getLogs] 완료 - projectId={}, appName={}, logLength={}", projectId, appName, logs.length());
        return logs;
    }

    @Transactional(readOnly = true)
    public AppResourceStatusResponseDto getAppDeploymentByProjectIdAndAppName(String projectId, String appName) {
        log.info("[getAppDeploymentByProjectIdAndAppName] 요청 - projectId={}, appName={}", projectId, appName);

        if(!projectClient.getProjectAvailable(projectId).data().status()) {
            log.warn("[getAppDeploymentByProjectIdAndAppName] 프로젝트 접근 권한 없음 - projectId={}", projectId);
            throw new ProjectAccessDeniedException();
        }

        AppDeploymentEntity appDeploymentEntityForStatus = appDeploymentRepository.findByProjectIdAndName(projectId, appName)
                .orElseThrow(AppDeploymentNotFoundException::new);

        AppDeploymentResourceStatusResponseDto.AppResourceDto appResourceDto =
                resourceClient.getAppDeploymentResourceStatus(projectId, Collections.singletonList(appName)).data().getFirst();

        AppResourceStatusResponseDto result = AppResourceStatusResponseDto.builder()
                    .appId(appDeploymentEntityForStatus.getId())
                    .cpuUsagePercentage(appResourceDto.cpu().percentage())
                    .memoryUsed(appResourceDto.memory().used())
                    .memoryTotal(appResourceDto.memory().limit())
                    .diskUsed(appResourceDto.disk().used())
                    .diskTotal(appResourceDto.disk().limit())
                    .currentInstances(appResourceDto.instance().used())
                    .availableInstances(appResourceDto.instance().limit())
                    .build();

        log.info("[getAppDeploymentByProjectIdAndAppName] 완료 - projectId={}, appName={}, cpuUsage={}, memoryUsed={}/{}",
                projectId, appName, result.cpuUsagePercentage(), result.memoryUsed(), result.memoryTotal());
        return result;
    }

    @Transactional(readOnly = true)
    public AppDeploymentInfoResponseDto getDetailsProjectIdAndAppName(String projectId, String appName) {
        log.info("[getDetailsProjectIdAndAppName] 요청 - projectId={}, appName={}", projectId, appName);

        if(!projectClient.getProjectAvailable(projectId).data().status()) {
            log.warn("[getDetailsProjectIdAndAppName] 프로젝트 접근 권한 없음 - projectId={}", projectId);
            throw new ProjectAccessDeniedException();
        }

        AppDeploymentEntity appDeploymentEntity = appDeploymentRepository.findByProjectIdAndName(projectId, appName)
                .orElseThrow(AppDeploymentNotFoundException::new);

        log.info("[getDetailsProjectIdAndAppName] 요청1111111 - appDeployment={}", appDeploymentEntity.getName());

        AppDeploymentResourceStatusResponseDto.AppResourceDto appResourceDto =
                resourceClient.getAppDeploymentResourceStatus(projectId, Collections.singletonList(appName)).data().getFirst();

        log.info("[getDetailsProjectIdAndAppName] 요청22222222222 - resource(CPU)={}", appResourceDto.cpu());

        int resourceUsePercentage = calculateWeightedResourceUsage(
                appResourceDto.memory().percentage(),
                appResourceDto.cpu().percentage(),
                appResourceDto.disk().percentage()
        );

        log.info("[getDetailsProjectIdAndAppName] 요청333333333333333 - resourceUsePercentage={}",  resourceUsePercentage);

        String githubRepositoryUrl = appDeploymentEntity.getGithubRepository() != null
                ? appDeploymentEntity.getGithubRepository().getRepositoryFullName()
                : null;

        AppDeploymentInfoResponseDto result = AppDeploymentInfoResponseDto.builder()
                .appId(appDeploymentEntity.getId())
                .port(appDeploymentEntity.getPort())
                .resourceUsePercentage(resourceUsePercentage)
                .githubRepositoryUrl(githubRepositoryUrl)
                .status(appDeploymentEntity.getStatus().name())
                .build();

        log.info("[getDetailsProjectIdAndAppName] 완료 - projectId={}, appName={}, status={}, resourceUsePercentage={}",
                projectId, appName, result.status(), result.resourceUsePercentage());
        return result;
    }

    @Transactional
    public void updateAppVersion(Long appDeploymentId, Integer version) {
        log.info("[updateAppVersion] 요청 - appDeploymentId={}, version={}", appDeploymentId, version);

        AppDeploymentEntity appDeploymentEntity = appDeploymentRepository.findById(appDeploymentId)
                .orElseThrow(AppDeploymentNotFoundException::new);

        if (!projectClient.getProjectOwner(appDeploymentEntity.getProjectId()).data().status()) {
            log.warn("[updateAppVersion] 프로젝트 오너 권한 없음 - projectId={}", appDeploymentEntity.getProjectId());
            throw new ProjectAccessDeniedException();
        }

        String tag = appDeploymentTagRepository
                .findByAppDeployment_IdAndVersion(appDeploymentId, version)
                .orElseThrow(AppDeploymentVersionNotFoundException::new)
                .getTag();

        String image = appDeploymentEntity.getProjectId()
                + "/" + appDeploymentEntity.getGithubRepository().getRepositoryId()
                + ":" + tag;

        resourceClient.updateAppImage(
                appDeploymentEntity.getProjectId(),
                appDeploymentEntity.getName(),
                UpdateAppImageRequestDto.builder()
                        .containers(Collections.singletonList(
                                UpdateAppImageRequestDto.ContainerDto.builder()
                                        .name(appDeploymentEntity.getName())
                                        .image(image)
                                        .build()
                        ))
                        .build()
        );

        log.info("[updateAppVersion] 완료 - appDeploymentId={}, version={}, image={}", appDeploymentId, version, image);
    }

    private int calculateWeightedResourceUsage(int memoryPercentage, int cpuPercentage, int diskPercentage) {
        double memoryWeight = 0.5;
        double cpuWeight = 0.3; 
        double diskWeight = 0.2;
        
        double weightedAverage = (memoryPercentage * memoryWeight) + 
                                (cpuPercentage * cpuWeight) + 
                                (diskPercentage * diskWeight);
        
        return Math.min((int) Math.round(weightedAverage), 100);
    }

    private String toMilliCpu(Double cpuCore) {
        return Math.round(cpuCore * 1000) + "m";
    }

    private String toMi(Double gb) {
        return Math.round(gb * MI_PER_GB) + "Mi";
    }

    private String toMi(Integer gb) {
        return (gb * MI_PER_GB) + "Mi";
    }
}
