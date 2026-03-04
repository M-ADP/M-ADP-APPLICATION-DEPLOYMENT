package madp.appdeployment.domain.application.service;

import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class JenkinsService {
    private final AppDeploymentRepository appDeploymentRepository;
    private final AppDeploymentTagRepository appDeploymentTagRepository;
    private final ResourceClient resourceClient;

    @Transactional
    public void successTrigger(JenkinsSuccessTriggerRequestDto jenkinsSuccessTriggerRequestDto) {
        AppDeploymentEntity appDeploymentEntity = appDeploymentRepository.findByGithubRepository_RepositoryId(jenkinsSuccessTriggerRequestDto.repositoryId()).orElseThrow(AppDeploymentNotFoundException::new);
        appDeploymentEntity.updateStatus(AppDeploymentStatus.DEPLOYING);

        AppDeploymentTagEntity appDeploymentTagEntity = AppDeploymentTagEntity.builder()
                .appDeployment(appDeploymentEntity)
                .version(appDeploymentEntity.getCurrentVersion() + 1)
                .tag(jenkinsSuccessTriggerRequestDto.tag())
                .build();

        appDeploymentTagRepository.save(appDeploymentTagEntity);

        appDeploymentEntity.upgradeVersion();

        String projectId = appDeploymentEntity.getProjectId();
        String imageName = jenkinsSuccessTriggerRequestDto.image();

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
                                        .build()
                        )
                )
                .build();

        resourceClient.createAppDeployment(projectId, appDeploymentRequestDto);
        // 비동기면 Controller 에서 webhook 받아야 할 듯
        appDeploymentEntity.updateStatus(AppDeploymentStatus.RUNNING);
    }

    @Transactional
    public void failTrigger(Long repositoryId) {
        AppDeploymentEntity appDeploymentEntity = appDeploymentRepository.findByGithubRepository_RepositoryId(repositoryId).orElseThrow(AppDeploymentNotFoundException::new);
        appDeploymentEntity.updateStatus(AppDeploymentStatus.FAILED);
    }
}
