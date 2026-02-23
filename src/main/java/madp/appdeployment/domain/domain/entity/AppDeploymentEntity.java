package madp.appdeployment.domain.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import madp.appdeployment.domain.domain.enums.AppDeploymentStatus;
import madp.appdeployment.domain.domain.vo.ResourceInfo;
import madp.appdeployment.domain.exception.InvalidAppDeploymentException;
import madp.appdeployment.global.entity.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "app_deployment",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"project_id", "name"})
        }
)
public class AppDeploymentEntity extends BaseEntity {
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "project_id", nullable = false)
    private String projectId;

    @Embedded
    private ResourceInfo resourceInfo;

    /**
     * GitHub Repository 정보
     * OneToOne: 하나의 배포는 하나의 GitHub Repository에만 연결됨
     * GitHubAllowedRepoEntity가 삭제되면 JPA CASCADE로 이 배포도 자동 삭제됨
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "github_repository_id", referencedColumnName = "repository_id")
    private GithubAllowedRepoEntity githubRepository;

    @Column(name = "github_branch")
    private String githubBranch;

    @Column(name = "port", nullable = false)
    private Integer port;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private AppDeploymentStatus status;

    @Column(name = "image")
    private String image;

    @Builder
    public AppDeploymentEntity(String name, String projectId, ResourceInfo resourceInfo, Integer port) {
        validateFields(name, projectId, resourceInfo, port);

        this.name = name;
        this.projectId = projectId;
        this.resourceInfo = resourceInfo;
        this.githubBranch = null;
        this.githubRepository = null;
        this.port = port;
        this.status = AppDeploymentStatus.PENDING;
        this.image = null;
    }

    public void uploadGithubInfo(String branch, GithubAllowedRepoEntity githubAllowedRepoEntity) {
        validateGithubInfo(branch, githubAllowedRepoEntity);
        
        this.githubRepository = githubAllowedRepoEntity;
        this.githubBranch = branch != null ? branch : "main";
    }

    public void updateStatus(AppDeploymentStatus status) {
        this.status = status;
    }
    
    private void validateGithubInfo(String branch, GithubAllowedRepoEntity githubAllowedRepoEntity) {
        if (githubAllowedRepoEntity == null) {
            throw new InvalidAppDeploymentException("GitHub Repository 정보는 필수입니다.");
        }
        
        if (branch != null && branch.trim().isEmpty()) {
            throw new InvalidAppDeploymentException("브랜치명은 공백일 수 없습니다.");
        }
    }

    private void validateFields(String name, String projectId, ResourceInfo resourceInfo, Integer port) {
        if (name == null) {
            throw new InvalidAppDeploymentException("앱 이름은 필수입니다.");
        }
        
        if (name.trim().isEmpty()) {
            throw new InvalidAppDeploymentException("앱 이름은 공백일 수 없습니다.");
        }
        
        if (projectId == null) {
            throw new InvalidAppDeploymentException("프로젝트 ID는 필수입니다.");
        }

        if(projectId.trim().isEmpty()) {
            throw new InvalidAppDeploymentException("프로젝트 ID는 공백일 수 없습니다.");
        }
        
        if (resourceInfo == null) {
            throw new InvalidAppDeploymentException("리소스 정보는 필수입니다.");
        }
        
        if (port == null) {
            throw new InvalidAppDeploymentException("포트는 필수입니다.");
        }
        
        if (port <= 0) {
            throw new InvalidAppDeploymentException("포트는 0보다 커야 합니다.");
        }
        
        if (port > 65535) {
            throw new InvalidAppDeploymentException("포트는 65535 이하이어야 합니다.");
        }
    }
}
