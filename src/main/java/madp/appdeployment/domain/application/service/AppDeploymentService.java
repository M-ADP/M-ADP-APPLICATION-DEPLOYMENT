package madp.appdeployment.domain.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import madp.appdeployment.domain.domain.entity.AppDeploymentEntity;
import madp.appdeployment.domain.domain.entity.GithubAllowedRepoEntity;
import madp.appdeployment.domain.domain.enums.AppDeploymentStatus;
import madp.appdeployment.domain.domain.repository.AppDeploymentRepository;
import madp.appdeployment.domain.domain.repository.GithubAllowedRepoRepository;
import madp.appdeployment.domain.domain.vo.ResourceInfo;
import madp.appdeployment.domain.exception.AppDeploymentNotFoundException;
import madp.appdeployment.domain.exception.GithubAllowedRepoNotFoundException;
import madp.appdeployment.domain.exception.ProjectAccessDeniedException;
import madp.appdeployment.domain.infrastructure.client.ProjectClient;
import madp.appdeployment.domain.infrastructure.client.ResourceClient;
import madp.appdeployment.domain.infrastructure.client.request.AppRevisionRequestDto;
import madp.appdeployment.domain.infrastructure.client.response.AppDeploymentResourceStatusResponseDto;
import madp.appdeployment.domain.infrastructure.client.response.PodLogsResponseDto;
import madp.appdeployment.domain.presentation.dto.request.CreateAppDeploymentRequestDto;
import madp.appdeployment.domain.presentation.dto.request.UpdateGithubInfoRequestDto;
import madp.appdeployment.domain.presentation.dto.response.AppDeploymentInfoResponseDto;
import madp.appdeployment.domain.presentation.dto.response.AppDeploymentListResponseDto;
import madp.appdeployment.domain.presentation.dto.response.AppDeploymentStatusResponseDto;
import madp.appdeployment.domain.presentation.dto.response.AppDeploymentSummaryResponseDto;
import madp.appdeployment.domain.presentation.dto.response.AppResourceStatusResponseDto;
import madp.appdeployment.global.presentation.dto.response.ApiResponseDto;
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
    private final AppDeploymentRepository appDeploymentRepository;
    private final GithubAllowedRepoRepository githubAllowedRepoRepository;
    private final ProjectClient projectClient;
    private final ResourceClient resourceClient;

    @Transactional
    public Long createAppDeployment(CreateAppDeploymentRequestDto createAppDeploymentRequestDto) {
        log.info("[createAppDeployment] 요청 - projectId={}, name={}, port={}, cpu={}, memory={}, disk={}",
                createAppDeploymentRequestDto.projectId(),
                createAppDeploymentRequestDto.name(),
                createAppDeploymentRequestDto.port(),
                createAppDeploymentRequestDto.cpu(),
                createAppDeploymentRequestDto.memory(),
                createAppDeploymentRequestDto.disk());

        if(!projectClient.getProjectOwner(createAppDeploymentRequestDto.projectId()).data().status()) {
            log.warn("[createAppDeployment] 프로젝트 오너 권한 없음 - projectId={}", createAppDeploymentRequestDto.projectId());
            throw new ProjectAccessDeniedException();
        }

        ResourceInfo resourceInfo = ResourceInfo.builder()
                .cpu(createAppDeploymentRequestDto.cpu())
                .disk(createAppDeploymentRequestDto.disk())
                .memory(createAppDeploymentRequestDto.memory())
                .build();

        AppDeploymentEntity appDeploymentEntity = AppDeploymentEntity.builder()
                .name(createAppDeploymentRequestDto.name())
                .port(createAppDeploymentRequestDto.port())
                .projectId(createAppDeploymentRequestDto.projectId())
                .resourceInfo(resourceInfo)
                .build();

        Long savedId = appDeploymentRepository.save(appDeploymentEntity).getId();
        log.info("[createAppDeployment] 완료 - appDeploymentId={}", savedId);
        return savedId;
    }

    @Transactional
    public void deleteAppDeployment(Long appDeploymentId) {
        log.info("[deleteAppDeployment] 요청 - appDeploymentId={}", appDeploymentId);

        AppDeploymentEntity appDeploymentEntity = appDeploymentRepository.findById(appDeploymentId)
                .orElseThrow(AppDeploymentNotFoundException::new);

        log.info("[deleteAppDeployment] 앱 조회 완료 - appDeploymentId={}, projectId={}, name={}",
                appDeploymentId, appDeploymentEntity.getProjectId(), appDeploymentEntity.getName());

        if(!projectClient.getProjectOwner(appDeploymentEntity.getProjectId()).data().status()) {
            log.warn("[deleteAppDeployment] 프로젝트 오너 권한 없음 - projectId={}", appDeploymentEntity.getProjectId());
            throw new ProjectAccessDeniedException();
        }

        resourceClient.deleteAppDeployment(appDeploymentEntity.getProjectId(), appDeploymentEntity.getName());
        log.info("[deleteAppDeployment] 리소스 삭제 요청 완료 - projectId={}, name={}", appDeploymentEntity.getProjectId(), appDeploymentEntity.getName());

        appDeploymentRepository.delete(appDeploymentEntity);
        log.info("[deleteAppDeployment] 완료 - appDeploymentId={}", appDeploymentId);
    }

    @Transactional
    public void updateAppDeploymentResourceInfo(Long appDeploymentId, ResourceInfo resourceInfo) {
        log.info("[updateAppDeploymentResourceInfo] 요청 - appDeploymentId={}, cpu={}, memory={}, disk={}",
                appDeploymentId, resourceInfo.getCpu(), resourceInfo.getMemory(), resourceInfo.getDisk());

        AppDeploymentEntity appDeploymentEntity = appDeploymentRepository.findById(appDeploymentId)
                .orElseThrow(AppDeploymentNotFoundException::new);

        if(!projectClient.getProjectOwner(appDeploymentEntity.getProjectId()).data().status()) {
            log.warn("[updateAppDeploymentResourceInfo] 프로젝트 오너 권한 없음 - projectId={}", appDeploymentEntity.getProjectId());
            throw new ProjectAccessDeniedException();
        }

        resourceClient.reviseApp(
                new AppRevisionRequestDto(
                        appDeploymentId.toString(),
                        resourceInfo.getCpu(),
                        resourceInfo.getMemory(),
                        resourceInfo.getDisk()
                )
        );

        appDeploymentEntity.updateResourceInfo(resourceInfo);
        log.info("[updateAppDeploymentResourceInfo] 완료 - appDeploymentId={}", appDeploymentId);
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
        GithubAllowedRepoEntity githubAllowedRepoEntity = githubAllowedRepoRepository.findByRepositoryFullName(repositoryFullName)
                .orElseThrow(GithubAllowedRepoNotFoundException::new);

        appDeploymentEntity.uploadGithubInfo(updateGithubInfoRequestDto.branch(), githubAllowedRepoEntity);
        log.info("[updateGithubInfo] 완료 - appDeploymentId={}, repositoryFullName={}", updateGithubInfoRequestDto.appDeploymentId(), repositoryFullName);
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
    public String getLogs(String projectId, String appName) {
        log.info("[getLogs] 요청 - projectId={}, appName={}", projectId, appName);

        if(!projectClient.getProjectAvailable(projectId).data().status()) {
            log.warn("[getLogs] 프로젝트 접근 권한 없음 - projectId={}", projectId);
            throw new ProjectAccessDeniedException();
        }

        ApiResponseDto<PodLogsResponseDto.LogDataDto> podLogsResponseDto = resourceClient.getPodLogs(projectId, appName);
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

        AppDeploymentResourceStatusResponseDto.AppResourceDto appResourceDto =
                resourceClient.getAppDeploymentResourceStatus(projectId, Collections.singletonList(appName)).data().getFirst();

        AppResourceStatusResponseDto result = AppResourceStatusResponseDto.builder()
                    .appId(Long.parseLong(appResourceDto.appId()))
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

        AppDeploymentResourceStatusResponseDto.AppResourceDto appResourceDto =
                resourceClient.getAppDeploymentResourceStatus(projectId, Collections.singletonList(appName)).data().getFirst();

        int resourceUsePercentage = calculateWeightedResourceUsage(
                appResourceDto.memory().percentage(),
                appResourceDto.cpu().percentage(),
                appResourceDto.disk().percentage()
        );

        AppDeploymentInfoResponseDto result = AppDeploymentInfoResponseDto.builder()
                .appId(Long.parseLong(appResourceDto.appId()))
                .port(appDeploymentEntity.getPort())
                .resourceUsePercentage(resourceUsePercentage)
                .githubRepositoryUrl(appDeploymentEntity.getGithubRepository().getRepositoryFullName())
                .status(appDeploymentEntity.getStatus().name())
                .build();

        log.info("[getDetailsProjectIdAndAppName] 완료 - projectId={}, appName={}, status={}, resourceUsePercentage={}",
                projectId, appName, result.status(), result.resourceUsePercentage());
        return result;
    }

    @Transactional(readOnly = true)
    public List<AppDeploymentListResponseDto> getAppDeploymentListByProjectId(Long projectId) {
        log.info("[getAppDeploymentListByProjectId] 요청 - projectId={}", projectId);

        if (!projectClient.getProjectAvailable(String.valueOf(projectId)).data().status()) {
            log.warn("[getAppDeploymentListByProjectId] 프로젝트 접근 권한 없음 - projectId={}", projectId);
            throw new ProjectAccessDeniedException();
        }

        List<AppDeploymentEntity> apps = appDeploymentRepository.findAllByProjectId(String.valueOf(projectId));
        List<String> names = apps.stream().map(AppDeploymentEntity::getName).toList();
        log.info("[getAppDeploymentListByProjectId] 조회된 앱 목록 - projectId={}, appNames={}", projectId, names);

        Map<String, AppDeploymentResourceStatusResponseDto.AppResourceDto> resourceMap =
                resourceClient.getAppDeploymentResourceStatus(String.valueOf(projectId), names).data().stream()
                        .collect(Collectors.toMap(AppDeploymentResourceStatusResponseDto.AppResourceDto::appId, Function.identity()));

        List<AppDeploymentListResponseDto> result = apps.stream().map(app -> {
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

        log.info("[getAppDeploymentListByProjectId] 완료 - projectId={}, resultCount={}", projectId, result.size());
        return result;
    }

    @Transactional(readOnly = true)
    public List<AppDeploymentSummaryResponseDto> getAppDeploymentSummary(List<Long> projectIds) {
        log.info("[getAppDeploymentSummary] 요청 - projectIds={}", projectIds);

        List<AppDeploymentSummaryResponseDto> result = projectIds.stream().map(projectId -> {
            List<AppDeploymentEntity> apps = appDeploymentRepository.findAllByProjectId(String.valueOf(projectId));

            int running = (int) apps.stream()
                    .filter(app -> app.getStatus() == AppDeploymentStatus.RUNNING)
                    .count();
            int warning = apps.size() - running;
            String state = (warning == 0) ? "RUNNING" : "STOPPED";

            log.info("[getAppDeploymentSummary] projectId={}, totalApps={}, running={}, warning={}, state={}",
                    projectId, apps.size(), running, warning, state);

            return AppDeploymentSummaryResponseDto.builder()
                    .projectId(projectId)
                    .running(running)
                    .warning(warning)
                    .state(state)
                    .build();
        }).toList();

        log.info("[getAppDeploymentSummary] 완료 - projectIds={}", projectIds);
        return result;
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
}
