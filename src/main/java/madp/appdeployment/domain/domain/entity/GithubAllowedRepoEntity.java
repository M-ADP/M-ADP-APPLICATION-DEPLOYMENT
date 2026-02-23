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
public class GithubAllowedRepoEntity extends BaseEntity {

    /**
     * GitHub App Installation 정보
     * ManyToOne: 여러 Repository가 하나의 Installation에 속할 수 있음
     * FetchType.LAZY: 필요할 때만 로딩 (N+1 문제 방지)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "github_installation_id", nullable = false)
    private GithubInstallationEntity installation;

    @Column(name = "repository_id", nullable = false, unique = true)
    private Long repositoryId;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    /**
     * Repository의 전체 이름 (owner/repository 형식)
     * 예: "facebook/react", "microsoft/typescript"
     * 
     * 저장 이유:
     * - GitHub API 호출 없이 바로 사용자에게 표시 가능
     * - 성능상 이점 (API 지연시간 없음)
     * - 대부분의 경우 repository 이름 변경은 자주 발생하지 않음
     * 
     * TODO: 향후 정확성이 중요해지면 GitHub API 실시간 조회로 변경 예정
     * - Repository webhook으로 이름 변경 감지 후 업데이트
     * - 또는 repositoryId 기반 GitHub API 호출로 최신 정보 조회
     */
    @Column(name = "repository_full_name", nullable = false)
    private String repositoryFullName;

    @Builder
    public GithubAllowedRepoEntity(GithubInstallationEntity installation, Long repositoryId, Long accountId, String repositoryFullName) {
        validateFields(installation, repositoryId, accountId, repositoryFullName);
        
        this.installation = installation;
        this.repositoryId = repositoryId;
        this.accountId = accountId;
        this.repositoryFullName = repositoryFullName;
    }

    private void validateFields(GithubInstallationEntity installation, Long repositoryId, Long accountId, String repositoryFullName) {
        if (installation == null) {
            throw new InvalidGitHubInstallationException("Installation은 필수입니다.");
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
        
        if (repositoryFullName == null || repositoryFullName.trim().isEmpty()) {
            throw new InvalidGitHubInstallationException("Repository Full Name은 필수입니다.");
        }
    }
}