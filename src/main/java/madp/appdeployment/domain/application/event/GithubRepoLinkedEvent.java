package madp.appdeployment.domain.application.event;

import lombok.Getter;
import madp.appdeployment.domain.domain.entity.AppDeploymentEntity;

@Getter
public class GithubRepoLinkedEvent {
    private final AppDeploymentEntity appDeployment;

    public GithubRepoLinkedEvent(AppDeploymentEntity appDeployment) {
        this.appDeployment = appDeployment;
    }
}
