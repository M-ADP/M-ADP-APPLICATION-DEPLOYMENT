package madp.appdeployment.domain.infrastructure.client;

import lombok.NonNull;
import madp.appdeployment.domain.infrastructure.client.fallback.GithubClientFallback;
import madp.appdeployment.domain.infrastructure.client.request.JenkinsDeploymentRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "jenkins-client",
        url = "jenkins-url", // 나중에 배포판에 바꿀 예정
        fallback = GithubClientFallback.class
)
public interface JenkinsClient {
    @PostMapping("/jenkins/callback/trigger")
    void triggerJenkins(@RequestBody @NonNull JenkinsDeploymentRequestDto jenkinsDeploymentRequestDto);
}
