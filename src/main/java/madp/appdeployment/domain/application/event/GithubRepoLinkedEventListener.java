package madp.appdeployment.domain.application.event;

import lombok.RequiredArgsConstructor;
import madp.appdeployment.domain.application.service.JenkinsService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GithubRepoLinkedEventListener {
    private final JenkinsService jenkinsService;

    @EventListener
    public void onGithubRepoLinked(GithubRepoLinkedEvent event) {
        jenkinsService.triggerBuild(event.getAppDeployment());
    }
}
