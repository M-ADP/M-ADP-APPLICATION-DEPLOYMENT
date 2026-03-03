package madp.appdeployment.domain.infrastructure.client;

import jakarta.validation.Valid;
import madp.appdeployment.domain.infrastructure.client.fallback.ProjectClientFallback;
import madp.appdeployment.domain.infrastructure.client.request.AppDeploymentRequestDto;
import madp.appdeployment.domain.infrastructure.client.response.AppDeploymentResourceStatusResponseDto;
import madp.appdeployment.domain.infrastructure.client.response.PodLogsResponseDto;
import madp.appdeployment.global.configuration.InternalServiceCommunicationConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
        name = "resource-client",
        fallback = ProjectClientFallback.class,
        configuration = InternalServiceCommunicationConfiguration.class
)
public interface ResourceClient {
    @PostMapping("/resource/apps/{projectId}")
    void createAppDeployment(@PathVariable String projectId, @RequestBody @Valid AppDeploymentRequestDto appDeploymentRequestDto);

    @GetMapping("/resource/apps/{projectId}/resource")
    AppDeploymentResourceStatusResponseDto getAppDeploymentResourceStatus(@PathVariable String projectId, @RequestParam(name = "names") List<String> appNames);

    @GetMapping("/resource/apps/{projectId}/{appName}/logs")
    PodLogsResponseDto getPodLogs(@PathVariable String projectId, @PathVariable String appName);
}
