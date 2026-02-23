package madp.appdeployment.domain.domain.repository;

import madp.appdeployment.domain.domain.entity.GithubInstallationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GithubInstallationRepository extends JpaRepository<GithubInstallationEntity, Long> {
    @Modifying
    @Query("delete from GithubInstallationEntity gi where gi.installationId = :installationId")
    void deleteAllByInstallationId(Long installationId);
    
    @Query("select gi from GithubInstallationEntity gi where gi.installationId = :installationId")
    Optional<GithubInstallationEntity> findByInstallationId(Long installationId);
    
    @Query("select gi from GithubInstallationEntity gi where gi.accountId = :accountId")
    Optional<GithubInstallationEntity> findByAccountId(Long accountId);
}
