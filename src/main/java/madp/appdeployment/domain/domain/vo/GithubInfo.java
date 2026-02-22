package madp.appdeployment.domain.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import madp.appdeployment.domain.exception.InvalidGithubInfoException;

/**
 * GitHub App 기반 Repository 정보 및 Webhook 설정
 * 
 * 플로우:
 * 1. GitHub App Installation → installationId 획득
 * 2. 사용자 repo 등록 → JWT 인증으로 webhook 등록 → webHookId/Secret 저장
 * 3. Push 발생 → webhook 수신 → Jenkins 트리거
 * 
 * installationId: owner별 고유, 중복 저장되지만 성능상 이점
 */
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GithubInfo {
    @Column(name = "github_account_id", nullable = false)
    private Long accountId;

    @Column(name = "github_repository_id", nullable = false)
    private Long repositoryId;

    @Column(name = "github_branch", nullable = false)
    private String branch;

    @Column(name = "github_webhook_id")
    private Long webHookId;

    @Column(name = "github_webhook_secret")
    private String webHookSecret;

    @Column(name = "github_installation_id")
    private Long installationId;

    @Builder
    public GithubInfo(Long accountId, Long repositoryId, String branch) {
        validateDefaultFields(accountId, repositoryId);

        this.accountId = accountId;
        this.repositoryId = repositoryId;
        this.branch = branch != null ? branch : "main";
        this.webHookId = null;
        this.webHookSecret = null;
    }

    public void updateWebHookInfo(Long webhookId, String webHookSecret) {
        validateAdditionalFields(webhookId, webHookSecret);
        
        this.webHookId = webhookId;
        this.webHookSecret = webHookSecret;
    }

    private void validateDefaultFields(Long accountId, Long repositoryId) {
        if (accountId == null) {
            throw new InvalidGithubInfoException("GitHub account ID는 필수입니다.");
        }
        
        if (accountId <= 0) {
            throw new InvalidGithubInfoException("GitHub account ID는 0보다 커야 합니다.");
        }
        
        if (repositoryId == null) {
            throw new InvalidGithubInfoException("GitHub repository ID는 필수입니다.");
        }
        
        if (repositoryId <= 0) {
            throw new InvalidGithubInfoException("GitHub repository ID는 0보다 커야 합니다.");
        }
    }

    private void validateAdditionalFields(Long webHookId, String webHookSecret) {
        if (webHookId == null) {
            throw new InvalidGithubInfoException("웹훅 ID는 필수입니다.");
        }
        
        if (webHookId <= 0) {
            throw new InvalidGithubInfoException("웹훅 ID는 0보다 커야 합니다.");
        }
        
        if (webHookSecret == null) {
            throw new InvalidGithubInfoException("웹훅 시크릿은 필수입니다.");
        }
        
        if (webHookSecret.trim().isEmpty()) {
            throw new InvalidGithubInfoException("웹훅 시크릿은 공백일 수 없습니다.");
        }
    }
}
