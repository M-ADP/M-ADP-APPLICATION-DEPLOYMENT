package madp.appdeployment.domain.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import madp.appdeployment.domain.exception.InvalidGitHubInstallationException;
import madp.appdeployment.global.entity.BaseEntity;

@Entity
@Table(name = "github_allowed_repo_user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GithubAllowedRepoUserEntity extends BaseEntity {
    
    @Column(name = "github_allowed_repo_id", nullable = false)
    private Long githubAllowedRepoId;
    
    @Column(name = "github_user_id", nullable = false)
    private Long githubUserId;
    
    @Builder
    public GithubAllowedRepoUserEntity(Long githubAllowedRepoId, Long githubUserId) {
        validateFields(githubAllowedRepoId, githubUserId);
        
        this.githubAllowedRepoId = githubAllowedRepoId;
        this.githubUserId = githubUserId;
    }
    
    private void validateFields(Long githubAllowedRepoId, Long githubUserId) {
        if (githubAllowedRepoId == null) {
            throw new InvalidGitHubInstallationException("GitHub allowed repo ID는 필수입니다.");
        }
        
        if (githubUserId == null) {
            throw new InvalidGitHubInstallationException("GitHub user ID는 필수입니다.");
        }
        
        if (githubUserId <= 0) {
            throw new InvalidGitHubInstallationException("GitHub user ID는 0보다 커야 합니다.");
        }
    }
}
