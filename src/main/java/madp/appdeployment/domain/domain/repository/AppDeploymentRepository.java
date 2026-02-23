package madp.appdeployment.domain.domain.repository;

import madp.appdeployment.domain.domain.entity.AppDeploymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppDeploymentRepository extends JpaRepository<AppDeploymentEntity, Long> {
    @Query("select ad from AppDeploymentEntity ad where ad.githubRepository.repositoryId = :repositoryId")
    Optional<AppDeploymentEntity> findByGithubRepository_RepositoryId(Long repositoryId);
}
