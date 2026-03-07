package madp.appdeployment.domain.application.service;

import lombok.RequiredArgsConstructor;
import madp.appdeployment.domain.domain.entity.AppDeploymentEntity;
import madp.appdeployment.domain.domain.entity.GithubAllowedRepoEntity;
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
import madp.appdeployment.domain.presentation.dto.response.AppDeploymentStatusResponseDto;
import madp.appdeployment.domain.presentation.dto.response.AppResourceStatusResponseDto;
import madp.appdeployment.global.presentation.dto.response.ApiResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppDeploymentService {
    private final AppDeploymentRepository appDeploymentRepository;
    private final GithubAllowedRepoRepository githubAllowedRepoRepository;
    private final ProjectClient projectClient;
    private final ResourceClient resourceClient;

    @Transactional
    public void createAppDeployment(CreateAppDeploymentRequestDto createAppDeploymentRequestDto) {
        if(!projectClient.getProjectOwner(createAppDeploymentRequestDto.projectId()).getData().status())
            throw new ProjectAccessDeniedException();

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

        appDeploymentRepository.save(appDeploymentEntity);
    }

    @Transactional
    public void deleteAppDeployment(Long appDeploymentId) {
        AppDeploymentEntity appDeploymentEntity = appDeploymentRepository.findById(appDeploymentId)
                .orElseThrow(AppDeploymentNotFoundException::new);

        // 프로젝트 오너인지 확인하도록 변경 - 현재는 프로젝트 멤버인지 판별하는 로직임
        if(!projectClient.getProjectOwner(appDeploymentEntity.getProjectId()).getData().status())
            throw new ProjectAccessDeniedException();

        // AppDeployment 삭제 시, 관련된 리소스(jenkins) 해제
        resourceClient.deleteAppDeployment(appDeploymentEntity.getProjectId(), appDeploymentEntity.getName());

        appDeploymentRepository.delete(appDeploymentEntity);

    }

    @Transactional
    public void updateAppDeploymentResourceInfo(Long appDeploymentId, ResourceInfo resourceInfo) {
        AppDeploymentEntity appDeploymentEntity = appDeploymentRepository.findById(appDeploymentId)
                .orElseThrow(AppDeploymentNotFoundException::new);

        if(!projectClient.getProjectOwner(appDeploymentEntity.getProjectId()).getData().status())
            throw new ProjectAccessDeniedException();

        resourceClient.reviseApp(
                new AppRevisionRequestDto(
                        appDeploymentId.toString(),
                        resourceInfo.getCpu(),
                        resourceInfo.getMemory(),
                        resourceInfo.getDisk()
                )
        );

        appDeploymentEntity.updateResourceInfo(resourceInfo);
    }

    @Transactional
    public void updateGithubInfo(UpdateGithubInfoRequestDto updateGithubInfoRequestDto) {
        AppDeploymentEntity appDeploymentEntity = appDeploymentRepository.findById(updateGithubInfoRequestDto.appDeploymentId())
                .orElseThrow(AppDeploymentNotFoundException::new);

        if(!projectClient.getProjectOwner(appDeploymentEntity.getProjectId()).getData().status())
            throw new ProjectAccessDeniedException();

        String repositoryFullName = updateGithubInfoRequestDto.owner() + "/" + updateGithubInfoRequestDto.repository();
        GithubAllowedRepoEntity githubAllowedRepoEntity = githubAllowedRepoRepository.findByRepositoryFullName(repositoryFullName).orElseThrow(GithubAllowedRepoNotFoundException::new);

        appDeploymentEntity.uploadGithubInfo(updateGithubInfoRequestDto.branch(), githubAllowedRepoEntity);
    }

    @Transactional(readOnly = true)
    public List<AppDeploymentStatusResponseDto> getAppDeploymentsByProjectId(String projectId) {
        if(!projectClient.getProjectAvailable(projectId).getData().status())
            throw new ProjectAccessDeniedException();

        List<AppDeploymentEntity> appDeploymentEntities = appDeploymentRepository.findAllByProjectId(projectId);
        List<String> names = appDeploymentEntities.stream().map(AppDeploymentEntity::getName).toList();
        ApiResponseDto<List<AppDeploymentResourceStatusResponseDto.AppResourceDto>> appDeploymentResourceStatusResponseDto =
                resourceClient.getAppDeploymentResourceStatus(projectId, names);
        Map<String, AppDeploymentEntity> appDeploymentEntityMap = appDeploymentEntities.stream()
                .collect(Collectors.toMap(AppDeploymentEntity::getName, Function.identity()));

        return appDeploymentResourceStatusResponseDto.getData().stream().map(
                (appResourceDto) -> {
                    AppDeploymentEntity appDeployment = appDeploymentEntityMap.get(appResourceDto.appId());
                    return AppDeploymentStatusResponseDto.builder()
                            .name(appResourceDto.appId())
                            .cpuUsagePercentage(appResourceDto.cpu().percentage())
                            .memoryUsagePercentage(appResourceDto.memory().percentage())
                            .port(appDeployment.getPort())
                            .podCount(appResourceDto.instance().used())
                            .build();
                }
        ).toList();
    }

    @Transactional(readOnly = true)
    public String getLogs(String projectId, String appName) {
        if(!projectClient.getProjectAvailable(projectId).getData().status())
            throw new ProjectAccessDeniedException();

        ApiResponseDto<PodLogsResponseDto.LogDataDto> podLogsResponseDto = resourceClient.getPodLogs(projectId, appName);
        return podLogsResponseDto.getData().podLogs().getFirst().logs();
    }

    @Transactional(readOnly = true)
    public AppResourceStatusResponseDto getAppDeploymentByProjectIdAndAppName(String projectId, String appName) {
        if(!projectClient.getProjectAvailable(projectId).getData().status())
            throw new ProjectAccessDeniedException();

        AppDeploymentResourceStatusResponseDto.AppResourceDto appResourceDto =
                resourceClient.getAppDeploymentResourceStatus(projectId, Collections.singletonList(appName)).getData().getFirst();

        return AppResourceStatusResponseDto.builder()
                    .appId(Long.parseLong(appResourceDto.appId()))
                    .cpuUsagePercentage(appResourceDto.cpu().percentage())
                    .memoryUsed(appResourceDto.memory().used())
                    .memoryTotal(appResourceDto.memory().limit())
                    .diskUsed(appResourceDto.disk().used())
                    .diskTotal(appResourceDto.disk().limit())
                    .currentInstances(appResourceDto.instance().used())
                    .availableInstances(appResourceDto.instance().limit())
                    .build();
    }

    @Transactional(readOnly = true)
    public AppDeploymentInfoResponseDto getDetailsProjectIdAndAppName(String projectId, String appName) {
        if(!projectClient.getProjectAvailable(projectId).getData().status())
            throw new ProjectAccessDeniedException();

        AppDeploymentEntity appDeploymentEntity = appDeploymentRepository.findByProjectIdAndName(projectId, appName)
                .orElseThrow(AppDeploymentNotFoundException::new);

        AppDeploymentResourceStatusResponseDto.AppResourceDto appResourceDto =
                resourceClient.getAppDeploymentResourceStatus(projectId, Collections.singletonList(appName)).getData().getFirst();

        int resourceUsePercentage = calculateWeightedResourceUsage(
                appResourceDto.memory().percentage(),
                appResourceDto.cpu().percentage(),
                appResourceDto.disk().percentage()
        );

        return AppDeploymentInfoResponseDto.builder()
                .appId(Long.parseLong(appResourceDto.appId()))
                .port(appDeploymentEntity.getPort())
                .resourceUsePercentage(resourceUsePercentage)
                .githubRepositoryUrl(appDeploymentEntity.getGithubRepository().getRepositoryFullName())
                .status(appDeploymentEntity.getStatus().name())
                .build();
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
