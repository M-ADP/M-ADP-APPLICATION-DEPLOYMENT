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
    private final AppDeploymentRepository appDeploymentRepository;
    private final AppDeploymentTagRepository appDeploymentTagRepository;
    private final ResourceClient resourceClient;

    @Transactional
    public void successTrigger(JenkinsSuccessTriggerRequestDto jenkinsSuccessTriggerRequestDto) {
        log.info("[successTrigger] 요청 - repositoryId={}, tag={}",
                jenkinsSuccessTriggerRequestDto.repositoryId(),
                jenkinsSuccessTriggerRequestDto.tag());

        AppDeploymentEntity appDeploymentEntity = appDeploymentRepository.findByGithubRepository_RepositoryId(jenkinsSuccessTriggerRequestDto.repositoryId())
                .orElseThrow(AppDeploymentNotFoundException::new);

        log.info("[successTrigger] 앱 조회 완료 - appDeploymentId={}, projectId={}, name={}, currentVersion={}",
                appDeploymentEntity.getId(),
                appDeploymentEntity.getProjectId(),
                appDeploymentEntity.getName(),
                appDeploymentEntity.getCurrentVersion());

        appDeploymentEntity.updateStatus(AppDeploymentStatus.DEPLOYING);

        AppDeploymentTagEntity appDeploymentTagEntity = AppDeploymentTagEntity.builder()
                .appDeployment(appDeploymentEntity)
                .version(appDeploymentEntity.getCurrentVersion() + 1)
                .tag(jenkinsSuccessTriggerRequestDto.tag())
                .build();

        appDeploymentTagRepository.save(appDeploymentTagEntity);
        appDeploymentEntity.upgradeVersion();

        String projectId = appDeploymentEntity.getProjectId();
        String imageName = projectId + "/" + jenkinsSuccessTriggerRequestDto.repositoryId();

        AppDeploymentRequestDto appDeploymentRequestDto = AppDeploymentRequestDto.builder()
                .name(projectId)
                .containers(
                        Collections.singletonList(
                                AppDeploymentRequestDto.ContainerDto.builder()
                                        .name(appDeploymentEntity.getName())
                                        .image(imageName + ":" + jenkinsSuccessTriggerRequestDto.tag())
                                        .ports(Collections.singletonList(appDeploymentEntity.getPort()))
                                        .resources(
                                                AppDeploymentRequestDto.ResourcesDto.builder()
                                                        .limits(
                                                                AppDeploymentRequestDto.ResourceDto.builder()
                                                                        .cpu(appDeploymentEntity.getResourceInfo().getCpu().toString())
                                                                        .memory(appDeploymentEntity.getResourceInfo().getMemory().toString())
                                                                        .build()
                                                        )
                                                        .build()
                                        )
                                        .disk(
                                                AppDeploymentRequestDto.DiskDto.builder()
                                                        .size(appDeploymentEntity.getResourceInfo().getDisk().toString())
                                                        .build()
                                        )
                                        .build()
                        )
                )
                .build();

        log.info("[successTrigger] 리소스 배포 요청 - projectId={}, name={}, image={}",
                projectId, appDeploymentEntity.getName(), imageName + ":" + jenkinsSuccessTriggerRequestDto.tag());

        resourceClient.createAppDeployment(projectId, appDeploymentRequestDto);
        appDeploymentEntity.updateStatus(AppDeploymentStatus.RUNNING);

        log.info("[successTrigger] 완료 - appDeploymentId={}, status=RUNNING, newVersion={}",
                appDeploymentEntity.getId(), appDeploymentEntity.getCurrentVersion());
    }

    @Transactional
    public void failTrigger(Long repositoryId) {
        log.info("[failTrigger] 요청 - repositoryId={}", repositoryId);

        AppDeploymentEntity appDeploymentEntity = appDeploymentRepository.findByGithubRepository_RepositoryId(repositoryId)
                .orElseThrow(AppDeploymentNotFoundException::new);

        appDeploymentEntity.updateStatus(AppDeploymentStatus.FAILED);

        log.info("[failTrigger] 완료 - appDeploymentId={}, projectId={}, name={}, status=FAILED",
                appDeploymentEntity.getId(), appDeploymentEntity.getProjectId(), appDeploymentEntity.getName());
    }
}
