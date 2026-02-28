package madp.appdeployment.domain.domain.repository;

import madp.appdeployment.domain.domain.entity.GithubAllowedRepoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GithubAllowedRepoRepository extends JpaRepository<GithubAllowedRepoEntity, Long> {
    @Modifying
    @Query("delete from GithubAllowedRepoEntity gar where gar.installation.installationId = :installationId")
    void deleteAllByInstallationId(Long installationId);

    @Modifying
    @Query("DELETE FROM GithubAllowedRepoEntity gar WHERE gar.repositoryId IN :repositoryIds")
    void deleteAllByRepositoryIdIn(List<Long> repositoryIds);

    /**
     * 주어진 계정 ID 들에 속하는 모든 Repository를 Installation과 함께 조회
     * fetch join을 통해 N+1 문제 해결 및 성능 최적화
     * 
     * @param accountIds GitHub 계정 ID 목록
     * @return Installation이 함께 로딩된 GitHubAllowedRepoEntity 목록
     */
    @Query("select gar from GithubAllowedRepoEntity gar " +
           "join fetch gar.installation " +
           "where gar.accountId IN :accountIds")
    List<GithubAllowedRepoEntity> findAllByAccountIdInWithInstallation(List<Long> accountIds);

    @Query("select gar from GithubAllowedRepoEntity gar where gar.repositoryFullName = :repositoryFullName")
    Optional<GithubAllowedRepoEntity> findByRepositoryFullName(String repositoryFullName);
}
