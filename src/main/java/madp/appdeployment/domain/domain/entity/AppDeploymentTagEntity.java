package madp.appdeployment.domain.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import madp.appdeployment.domain.exception.InvalidAppDeploymentTagException;
import madp.appdeployment.global.entity.BaseEntity;

@Getter
@Entity
@Table(name = "app_deployment_tag", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"app_deployment_id", "tag"}),
        @UniqueConstraint(columnNames = {"app_deployment_id", "version"})
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AppDeploymentTagEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    private AppDeploymentEntity appDeployment;

    @Column(nullable = false)
    private String tag;

    @Column(nullable = false)
    private Integer version;

    @Builder
    public AppDeploymentTagEntity(AppDeploymentEntity appDeployment, String tag, Integer version) {
        if (appDeployment == null) {
            throw new InvalidAppDeploymentTagException("AppDeployment는 null일 수 없습니다.");
        }
        if (tag == null || tag.trim().isEmpty()) {
            throw new InvalidAppDeploymentTagException("태그는 null이거나 빈 값일 수 없습니다.");
        }
        if (version == null) {
            throw new InvalidAppDeploymentTagException("버전은 null일 수 없습니다.");
        }
        if (version < 0) {
            throw new InvalidAppDeploymentTagException("버전은 음수일 수 없습니다.");
        }
        
        this.appDeployment = appDeployment;
        this.tag = tag;
        this.version = version;
    }
}
