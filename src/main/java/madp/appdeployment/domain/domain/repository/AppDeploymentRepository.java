package madp.appdeployment.domain.domain.repository;

import madp.appdeployment.domain.domain.entity.AppDeploymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppDeploymentRepository extends JpaRepository<AppDeploymentEntity, Long> {
    @Query("select ad from AppDeploymentEntity ad " +
           "join fetch ad.githubRepository gr " +
           "join fetch gr.installation " +
           "where gr.repositoryId = :repositoryId")
    Optional<AppDeploymentEntity> findByGithubRepository_RepositoryId(Long repositoryId);

    @Modifying
    @Query("delete from AppDeploymentEntity ad where ad.githubRepository.installation.id = :installationId")
    void deleteAllByInstallationId(Long installationId);

    @Query("select ad from AppDeploymentEntity ad where ad.projectId = :projectId")
    List<AppDeploymentEntity> findAllByProjectId(String projectId);

    @Query("select ad from AppDeploymentEntity ad where ad.projectId = :projectId and ad.name = :name")
    Optional<AppDeploymentEntity> findByProjectIdAndName(String projectId, String name);
}
