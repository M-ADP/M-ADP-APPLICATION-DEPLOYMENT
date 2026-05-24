package madp.appdeployment.domain.infrastructure.client;

import madp.appdeployment.domain.infrastructure.client.fallback.JenkinsClientFallback;
import madp.appdeployment.domain.infrastructure.client.response.JenkinsBuildsResponse;
import madp.appdeployment.domain.infrastructure.client.response.JenkinsCrumbResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "jenkins-client",
        fallback = JenkinsClientFallback.class
)
public interface JenkinsClient {
    @PostMapping("/job/app-deployment-pipeline/buildWithParameters")
    void triggerJenkins(
            @RequestParam("project_id") String projectId,
            @RequestParam("app_id") Long appId,
            @RequestParam("repository_full_name") String repositoryFullName,
            @RequestParam("repository_id") Long repositoryId,
            @RequestParam("branch") String branch,
            @RequestParam("installation_id") Long installationId,
            @RequestHeader("Authorization") String authorization,
            @RequestHeader("Jenkins-Crumb") String crumb
    );

    @GetMapping("/crumbIssuer/api/json")
    JenkinsCrumbResponseDto getCrumb(@RequestHeader("Authorization") String authorization);

    @GetMapping("/job/app-deployment-pipeline/api/json")
    JenkinsBuildsResponse getBuilds(
            @RequestParam("tree") String tree,
            @RequestHeader("Authorization") String authorization
    );

    @GetMapping("/job/app-deployment-pipeline/{buildNumber}/consoleText")
    String getConsoleLog(
            @PathVariable("buildNumber") Integer buildNumber,
            @RequestHeader("Authorization") String authorization
    );
}
