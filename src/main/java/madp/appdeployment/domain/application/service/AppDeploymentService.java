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
import madp.appdeployment.domain.presentation.dto.request.CreateAppDeploymentRequestDto;
import madp.appdeployment.domain.presentation.dto.request.UpdateGithubInfoRequestDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AppDeploymentService {
    private final AppDeploymentRepository appDeploymentRepository;
    private final GithubAllowedRepoRepository githubAllowedRepoRepository;
    private final ProjectClient projectClient;

    @Transactional
    public void createAppDeployment(CreateAppDeploymentRequestDto createAppDeploymentRequestDto) {
        if(!projectClient.getProjectAvailable(createAppDeploymentRequestDto.projectId()).status())
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
    public void updateGithubInfo(UpdateGithubInfoRequestDto updateGithubInfoRequestDto) {
        AppDeploymentEntity appDeploymentEntity = appDeploymentRepository.findById(updateGithubInfoRequestDto.appDeploymentId())
                .orElseThrow(AppDeploymentNotFoundException::new);

        if(!projectClient.getProjectAvailable(appDeploymentEntity.getProjectId()).status())
            throw new ProjectAccessDeniedException();

        String repositoryFullName = updateGithubInfoRequestDto.owner() + "/" + updateGithubInfoRequestDto.repository();
        GithubAllowedRepoEntity githubAllowedRepoEntity = githubAllowedRepoRepository.findByRepositoryFullName(repositoryFullName).orElseThrow(GithubAllowedRepoNotFoundException::new);

        appDeploymentEntity.uploadGithubInfo(updateGithubInfoRequestDto.branch(), githubAllowedRepoEntity);
    }
}
