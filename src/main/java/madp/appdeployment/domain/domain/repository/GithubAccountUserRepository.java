package madp.appdeployment.domain.domain.repository;

import madp.appdeployment.domain.domain.entity.GithubAccountUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GithubAccountUserRepository extends JpaRepository<GithubAccountUserEntity, Long> {
    @Modifying
    @Query("delete from GithubAccountUserEntity gau where gau.installation.accountId = :githubAccountId and gau.githubUserId = :githubUserId")
    void deleteByGithubAccountIdAndGithubUserId(Long githubAccountId, Long githubUserId);

    /**
     * 사용자가 속한 모든 GitHub 계정 ID 조회
     * 
     * @param githubUserId GitHub 사용자 ID
     * @return 사용자가 속한 GitHub 계정 ID 목록
     */
    @Query("select gau.installation.accountId from GithubAccountUserEntity gau where gau.githubUserId = :githubUserId")
    List<Long> findAllGithubAccountIdByGithubUserId(Long githubUserId);

    @Modifying
    @Query("delete from GithubAccountUserEntity gau where gau.installation.installationId = :installationId")
    void deleteAllByInstallationId(Long installationId);
}