package madp.appdeployment.domain.infrastructure.client.fallback;

import lombok.NonNull;
import madp.appdeployment.domain.exception.JenkinsUnavailableException;
import madp.appdeployment.domain.infrastructure.client.JenkinsClient;
import madp.appdeployment.domain.infrastructure.client.request.JenkinsDeploymentRequestDto;
import org.springframework.stereotype.Component;

@Component
public class JenkinsClientFallback implements JenkinsClient {

    @Override
    public void triggerJenkins(@NonNull JenkinsDeploymentRequestDto jenkinsDeploymentRequestDto) {
        throw new JenkinsUnavailableException();
    }
}
