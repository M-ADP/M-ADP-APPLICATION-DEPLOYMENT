package madp.appdeployment.domain.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import madp.appdeployment.domain.domain.entity.AppDeploymentEntity;
import madp.appdeployment.domain.domain.entity.AppDeploymentTagEntity;
import madp.appdeployment.domain.domain.enums.AppDeploymentStatus;
import madp.appdeployment.domain.domain.repository.AppDeploymentRepository;
import madp.appdeployment.domain.domain.repository.AppDeploymentTagRepository;
import madp.appdeployment.domain.exception.AppDeploymentNotFoundException;
import madp.appdeployment.domain.infrastructure.client.ResourceClient;
import madp.appdeployment.domain.infrastructure.client.request.AppDeploymentRequestDto;
import madp.appdeployment.domain.presentation.dto.request.JenkinsSuccessTriggerRequestDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class JenkinsService {
    private static final int MI_PER_GB = 1024;
    private final AppDeploymentRepository appDeploymentRepository;
    private final AppDeploymentTagRepository appDeploymentTagRepository;
    private final ResourceClient resourceClient;

    @Transactional
    public void successTrigger(JenkinsSuccessTriggerRequestDto jenkinsSuccessTriggerRequestDto) {
        log.info("[successTrigger] 요청 - repositoryId={}, tag={}, port={}",
                jenkinsSuccessTriggerRequestDto.repositoryId(), jenkinsSuccessTriggerRequestDto.tag(),
                jenkinsSuccessTriggerRequestDto.port());

        AppDeploymentEntity appDeploymentEntity = appDeploymentRepository.findByGithubRepository_RepositoryId(jenkinsSuccessTriggerRequestDto.repositoryId()).orElseThrow(AppDeploymentNotFoundException::new);

        log.info("[successTrigger] 앱 조회 완료 - appDeploymentId={}, projectId={}, name={}, currentVersion={}",
                appDeploymentEntity.getId(), appDeploymentEntity.getProjectId(),
                appDeploymentEntity.getName(), appDeploymentEntity.getCurrentVersion());

        appDeploymentEntity.updateStatus(AppDeploymentStatus.DEPLOYING);

        AppDeploymentTagEntity appDeploymentTagEntity = AppDeploymentTagEntity.builder()
                .appDeployment(appDeploymentEntity)
                .version(appDeploymentEntity.getCurrentVersion() + 1)
                .tag(jenkinsSuccessTriggerRequestDto.tag())
                .build();

        appDeploymentTagRepository.save(appDeploymentTagEntity);

        appDeploymentEntity.upgradeVersion();
        appDeploymentEntity.updatePort(jenkinsSuccessTriggerRequestDto.port());

        String projectId = appDeploymentEntity.getProjectId();
        String imageName = projectId + "/" + jenkinsSuccessTriggerRequestDto.repositoryId();

        AppDeploymentRequestDto appDeploymentRequestDto = AppDeploymentRequestDto.builder()
                .deploymentId(appDeploymentEntity.getId())
                .name(appDeploymentEntity.getName())
                .containers(
                        Collections.singletonList(
                                AppDeploymentRequestDto.ContainerDto.builder()
                                        .name(appDeploymentEntity.getName())
                                        .image(imageName + ":" + jenkinsSuccessTriggerRequestDto.tag())
                                        .ports(Collections.singletonList(jenkinsSuccessTriggerRequestDto.port()))
                                        .resources(
                                                AppDeploymentRequestDto.ResourcesDto.builder()
                                                        .limits(
                                                                AppDeploymentRequestDto.ResourceDto.builder()
                                                                        .cpu(toMilliCpu(appDeploymentEntity.getResourceInfo().getCpu()))
                                                                        .memory(toMi(appDeploymentEntity.getResourceInfo().getMemory()))
                                                                        .build()
                                                        )
                                                        .build()
                                        )
                                        .disk(
                                                AppDeploymentRequestDto.DiskDto.builder()
                                                        .size(toMi(appDeploymentEntity.getResourceInfo().getDisk()))
                                                        .build()
                                        )
                                        .build()
                        )
                )
                .build();

        log.info("[successTrigger] 리소스 배포 요청 - projectId={}, name={}, image={}",
                projectId, appDeploymentEntity.getName(), imageName + ":" + jenkinsSuccessTriggerRequestDto.tag());

        resourceClient.createAppDeployment(projectId, appDeploymentRequestDto);
        // 비동기면 Controller 에서 webhook 받아야 할 듯
        appDeploymentEntity.updateStatus(AppDeploymentStatus.RUNNING);
        log.info("[successTrigger] 완료 - appDeploymentId={}, status=RUNNING, newVersion={}",
                appDeploymentEntity.getId(), appDeploymentEntity.getCurrentVersion());
    }

    @Transactional
    public void failTrigger(Long repositoryId) {
        log.info("[failTrigger] 요청 - repositoryId={}", repositoryId);

        AppDeploymentEntity appDeploymentEntity = appDeploymentRepository.findByGithubRepository_RepositoryId(repositoryId).orElseThrow(AppDeploymentNotFoundException::new);
        appDeploymentEntity.updateStatus(AppDeploymentStatus.FAILED);

        log.info("[failTrigger] 완료 - appDeploymentId={}, projectId={}, name={}, status=FAILED",
                appDeploymentEntity.getId(), appDeploymentEntity.getProjectId(), appDeploymentEntity.getName());
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
