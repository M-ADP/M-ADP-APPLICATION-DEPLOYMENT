package madp.appdeployment.domain.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import madp.appdeployment.global.entity.BaseEntity;

@Getter
@Entity
@Table(name = "app_deployment_secret", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"app_deployment_id", "name"})
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AppDeploymentSecretEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_deployment_id", nullable = false)
    private AppDeploymentEntity appDeployment;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "value", nullable = false)
    private String value;

    @Builder
    public AppDeploymentSecretEntity(AppDeploymentEntity appDeployment, String name, String value) {
        if (appDeployment == null) {
            throw new IllegalArgumentException("AppDeployment는 null일 수 없습니다.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Secret 이름은 null이거나 빈 값일 수 없습니다.");
        }
        if (value == null) {
            throw new IllegalArgumentException("Secret 값은 null일 수 없습니다.");
        }
        this.appDeployment = appDeployment;
        this.name = name;
        this.value = value;
    }
}