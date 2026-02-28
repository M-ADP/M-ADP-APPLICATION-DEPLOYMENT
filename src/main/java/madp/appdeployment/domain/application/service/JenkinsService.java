package madp.appdeployment.domain.application.service;

import lombok.RequiredArgsConstructor;
import madp.appdeployment.domain.domain.entity.AppDeploymentEntity;
import madp.appdeployment.domain.domain.enums.AppDeploymentStatus;
import madp.appdeployment.domain.domain.repository.AppDeploymentRepository;
import madp.appdeployment.domain.exception.AppDeploymentNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JenkinsService {
    private final AppDeploymentRepository appDeploymentRepository;

    @Transactional
    public void successTrigger(Long repositoryId) {
        AppDeploymentEntity appDeploymentEntity = appDeploymentRepository.findByGithubRepository_RepositoryId(repositoryId).orElseThrow(AppDeploymentNotFoundException::new);
        appDeploymentEntity.updateStatus(AppDeploymentStatus.RUNNING);
    }

    @Transactional
    public void failTrigger(Long repositoryId) {
        AppDeploymentEntity appDeploymentEntity = appDeploymentRepository.findByGithubRepository_RepositoryId(repositoryId).orElseThrow(AppDeploymentNotFoundException::new);
        appDeploymentEntity.updateStatus(AppDeploymentStatus.FAILED);
    }
}
