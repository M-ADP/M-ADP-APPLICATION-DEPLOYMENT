package madp.appdeployment.domain.infrastructure.client.fallback;

import lombok.NonNull;
import madp.appdeployment.domain.exception.JenkinsUnavailableException;
import madp.appdeployment.domain.infrastructure.client.JenkinsClient;
import madp.appdeployment.domain.infrastructure.client.request.JenkinsDeploymentRequestDto;
import madp.appdeployment.domain.infrastructure.client.response.JenkinsCrumbResponseDto;
import org.springframework.stereotype.Component;

@Component
public class JenkinsClientFallback implements JenkinsClient {

    @Override
    public void triggerJenkins(@NonNull JenkinsDeploymentRequestDto jenkinsDeploymentRequestDto, String authorization, String crumb) {
        throw new JenkinsUnavailableException();
    }

    @Override
    public JenkinsCrumbResponseDto getCrumb(String authorization) {
        throw new JenkinsUnavailableException();
    }
}
