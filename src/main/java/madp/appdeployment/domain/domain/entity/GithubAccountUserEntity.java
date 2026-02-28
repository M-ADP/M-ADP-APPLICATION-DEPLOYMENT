package madp.appdeployment.domain.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import madp.appdeployment.domain.exception.InvalidGithubAccountUserException;
import madp.appdeployment.global.entity.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "github_account_user")
public class GithubAccountUserEntity extends BaseEntity {
    /**
     * GitHub App Installation 정보
     * ManyToOne: 여러 사용자가 하나의 Installation에 속할 수 있음
     * FetchType.LAZY: 필요할 때만 로딩 (N+1 문제 방지)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "github_installation_id", nullable = false)
    private GithubInstallationEntity installation;

    @Column(name = "github_user_id")
    private Long githubUserId;

    @Builder
    public GithubAccountUserEntity(GithubInstallationEntity installation, Long githubUserId) {
        validateFields(installation, githubUserId);
        this.installation = installation;
        this.githubUserId = githubUserId;
    }

    private void validateFields(GithubInstallationEntity installation, Long githubUserId) {
        if (installation == null) {
            throw new InvalidGithubAccountUserException("Installation은 필수입니다.");
        }
        if (githubUserId == null || githubUserId <= 0) {
            throw new InvalidGithubAccountUserException("GitHub User ID는 필수이며 0보다 커야 합니다.");
        }
    }
}