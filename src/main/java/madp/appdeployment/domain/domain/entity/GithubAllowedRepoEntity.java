package madp.appdeployment.domain.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import madp.appdeployment.domain.exception.InvalidGitHubInstallationException;
import madp.appdeployment.global.entity.BaseEntity;

/**
 * GitHub App Installation 에서 허용된 Repository 정보
 */
@Entity
@Table(name = "github_allowed_repository")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GitHubAllowedRepoEntity extends BaseEntity {

    @Column(name = "installation_id", nullable = false)
    private Long installationId;

    @Column(name = "repository_id", nullable = false, unique = true)
    private Long repositoryId;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Builder
    public GitHubAllowedRepoEntity(Long installationId, Long repositoryId, Long accountId) {
        validateFields(installationId, repositoryId, accountId);
        
        this.installationId = installationId;
        this.repositoryId = repositoryId;
        this.accountId = accountId;
    }

    private void validateFields(Long installationId, Long repositoryId, Long accountId) {
        if (installationId == null) {
            throw new InvalidGitHubInstallationException("Installation ID는 필수입니다.");
        }

        if (repositoryId == null) {
            throw new InvalidGitHubInstallationException("Repository ID는 필수입니다.");
        }
        
        if (accountId == null) {
            throw new InvalidGitHubInstallationException("Account ID는 필수입니다.");
        }
        
        if (accountId <= 0) {
            throw new InvalidGitHubInstallationException("Account ID는 0보다 커야 합니다.");
        }
    }
}