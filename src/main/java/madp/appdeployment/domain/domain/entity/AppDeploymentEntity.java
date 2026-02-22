package madp.appdeployment.domain.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import madp.appdeployment.domain.domain.enums.AppDeploymentStatus;
import madp.appdeployment.domain.domain.vo.GithubInfo;
import madp.appdeployment.domain.domain.vo.ResourceInfo;
import madp.appdeployment.domain.exception.InvalidAppDeploymentException;
import madp.appdeployment.global.entity.BaseEntity;

@Entity
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
    private Long projectId;

    @Embedded
    private ResourceInfo resourceInfo;

    @Embedded
    private GithubInfo githubInfo;

    @Column(name = "port", nullable = false)
    private Integer port;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private AppDeploymentStatus status;

    @Column(name = "image")
    private String image;

    @Builder
    public AppDeploymentEntity(String name, Long projectId, ResourceInfo resourceInfo, Integer port) {
        validateFields(name, projectId, resourceInfo, port);

        this.name = name;
        this.projectId = projectId;
        this.resourceInfo = resourceInfo;
        this.githubInfo = null;
        this.port = port;
        this.status = AppDeploymentStatus.PENDING;
        this.image = null;
    }

    private void validateFields(String name, Long projectId, ResourceInfo resourceInfo, Integer port) {
        if (name == null) {
            throw new InvalidAppDeploymentException("앱 이름은 필수입니다.");
        }
        
        if (name.trim().isEmpty()) {
            throw new InvalidAppDeploymentException("앱 이름은 공백일 수 없습니다.");
        }
        
        if (projectId == null) {
            throw new InvalidAppDeploymentException("프로젝트 ID는 필수입니다.");
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

    public void updateGithubInfo(GithubInfo githubInfo) {
        if (githubInfo == null) {
            throw new InvalidAppDeploymentException("GitHub 정보는 필수입니다.");
        }
        this.githubInfo = githubInfo;
    }
}
