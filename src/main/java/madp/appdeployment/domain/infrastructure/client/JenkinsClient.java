package madp.appdeployment.domain.infrastructure.client;

import lombok.NonNull;
import madp.appdeployment.domain.infrastructure.client.fallback.JenkinsClientFallback;
import madp.appdeployment.domain.infrastructure.client.request.JenkinsDeploymentRequestDto;
import madp.appdeployment.domain.infrastructure.client.response.JenkinsCrumbResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "jenkins-client",
        fallback = JenkinsClientFallback.class
)
public interface JenkinsClient {
    @PostMapping("/callback/trigger")
    void triggerJenkins(@RequestBody @NonNull JenkinsDeploymentRequestDto jenkinsDeploymentRequestDto, @RequestHeader("Authorization") String authorization, @RequestHeader("Jenkins-Crumb") String crumb);

    @GetMapping("/crumbIssuer/api/json")
    JenkinsCrumbResponseDto getCrumb(@RequestHeader("Authorization") String authorization);
}
