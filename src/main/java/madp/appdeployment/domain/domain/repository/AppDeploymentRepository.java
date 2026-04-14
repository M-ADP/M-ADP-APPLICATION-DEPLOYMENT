package madp.appdeployment.domain.domain.repository;

import madp.appdeployment.domain.domain.entity.AppDeploymentEntity;
import madp.appdeployment.domain.domain.repository.dto.ProjectResourceUsageSumDto;
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

    @Query("""
            select new madp.appdeployment.domain.domain.repository.dto.ProjectResourceUsageSumDto(
                coalesce(sum(ad.resourceInfo.cpu), 0.0),
                coalesce(sum(ad.resourceInfo.memory), 0.0),
                coalesce(sum(ad.resourceInfo.disk), 0)
            )
            from AppDeploymentEntity ad
            where ad.projectId = :projectId
            """)
    ProjectResourceUsageSumDto sumResourceUsageByProjectId(String projectId);

    @Query("""
            select new madp.appdeployment.domain.domain.repository.dto.ProjectResourceUsageSumDto(
                coalesce(sum(ad.resourceInfo.cpu), 0.0),
                coalesce(sum(ad.resourceInfo.memory), 0.0),
                coalesce(sum(ad.resourceInfo.disk), 0)
            )
            from AppDeploymentEntity ad
            where ad.projectId = :projectId
              and ad.id <> :excludeAppId
            """)
    ProjectResourceUsageSumDto sumResourceUsageByProjectIdExcludingAppId(String projectId, Long excludeAppId);

    @Query("select ad.projectId from AppDeploymentEntity ad where ad.id = :appDeploymentId")
    Optional<String> findProjectIdById(Long appDeploymentId);

    @Query("select count(ad) > 0 from AppDeploymentEntity ad where ad.githubRepository.repositoryId = :repositoryId and ad.id <> :excludeAppId")
    boolean existsByGithubRepositoryIdExcludingAppId(Long repositoryId, Long excludeAppId);

    @Modifying
    @Query("update AppDeploymentEntity ad set ad.githubRepository = null, ad.githubBranch = null where ad.projectId = :projectId and ad.githubRepository is not null")
    void disconnectGithubRepositoriesByProjectId(String projectId);
}
