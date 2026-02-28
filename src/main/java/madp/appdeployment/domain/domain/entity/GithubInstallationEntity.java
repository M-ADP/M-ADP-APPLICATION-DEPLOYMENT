package madp.appdeployment.domain.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import madp.appdeployment.domain.domain.enums.GitHubAccountType;
import madp.appdeployment.domain.exception.InvalidGitHubInstallationException;
import madp.appdeployment.global.entity.BaseEntity;

/**
 * GitHub App Installation 정보
 * <p>
 * owner (organization/user)별로 installationId 매핑
 * 사용자가 GitHub 정보 등록 시 해당 owner의 installationId로 webhook 등록
 */
@Entity
@Table(name = "github_installation")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GithubInstallationEntity extends BaseEntity {

    @Column(name = "installation_id", nullable = false, unique = true)
    private Long installationId;

    @Column(name = "account_id", nullable = false, unique = true)
    private Long accountId;

    @Column(name = "account_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private GitHubAccountType accountType;

    @Column(name = "avatar_url")
    private String avatarUrl; // Organization 또는 User 프로필 사진 URL

    @Builder
    public GithubInstallationEntity(Long installationId, Long accountId, GitHubAccountType accountType, String avatarUrl) {
        validateFields(installationId, accountId, accountType, avatarUrl);
        
        this.installationId = installationId;
        this.accountId = accountId;
        this.accountType = accountType;
        this.avatarUrl = avatarUrl;
    }

    private void validateFields(Long installationId, Long accountId, GitHubAccountType accountType, String avatarUrl) {
        if (installationId == null) {
            throw new InvalidGitHubInstallationException("Installation ID는 필수입니다.");
        }
        
        if (installationId <= 0) {
            throw new InvalidGitHubInstallationException("Installation ID는 0보다 커야 합니다.");
        }
        
        if (accountId == null) {
            throw new InvalidGitHubInstallationException("GitHub account ID는 필수입니다.");
        }
        
        if (accountId <= 0) {
            throw new InvalidGitHubInstallationException("GitHub account ID는 0보다 커야 합니다.");
        }
        
        if (accountType == null) {
            throw new InvalidGitHubInstallationException("Account type은 필수입니다.");
        }
        
        if (avatarUrl != null && avatarUrl.trim().isEmpty()) {
            throw new InvalidGitHubInstallationException("Avatar URL이 빈 문자열일 수 없습니다.");
        }
    }
}