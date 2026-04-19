package madp.appdeployment.domain.infrastructure.client;

import jakarta.validation.Valid;
import madp.appdeployment.domain.infrastructure.client.fallback.ResourceClientFallback;
import madp.appdeployment.domain.infrastructure.client.request.AppRevisionRequestDto;
import madp.appdeployment.domain.infrastructure.client.request.AppDeploymentRequestDto;
import madp.appdeployment.domain.infrastructure.client.request.CreateSecretRequestDto;
import madp.appdeployment.domain.infrastructure.client.response.AppDeploymentResourceStatusResponseDto;
import madp.appdeployment.domain.infrastructure.client.response.AppRevisionResponseDto;
import madp.appdeployment.domain.infrastructure.client.response.DeleteAppDeploymentResponseDto;
import madp.appdeployment.domain.infrastructure.client.response.PodLogsResponseDto;
import madp.appdeployment.domain.infrastructure.client.response.SecretCreationResponseDto;
import madp.appdeployment.domain.presentation.dto.response.AppBuildLogDetailResponseDto;
import madp.appdeployment.domain.presentation.dto.response.AppBuildLogListResponseDto;
import madp.appdeployment.global.configuration.InternalServiceCommunicationConfiguration;
import madp.appdeployment.global.presentation.dto.response.ApiResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
        name = "resource-client",
        fallback = ResourceClientFallback.class,
        configuration = InternalServiceCommunicationConfiguration.class
)
public interface ResourceClient {
    @PostMapping("/apps/{projectId}")
    void createAppDeployment(@PathVariable String projectId, @RequestBody @Valid AppDeploymentRequestDto appDeploymentRequestDto);

    @GetMapping("/apps/{projectId}/resource")
    ApiResponseDto<List<AppDeploymentResourceStatusResponseDto.AppResourceDto>> getAppDeploymentResourceStatus(
            @PathVariable String projectId,
            @RequestParam(name = "names") List<String> appNames
    );

    @GetMapping("/apps/{projectId}/{appName}/logs")
    ApiResponseDto<PodLogsResponseDto.LogDataDto> getPodLogs(@PathVariable String projectId, @PathVariable String appName);

    @PatchMapping("/apps")
    ApiResponseDto<AppRevisionResponseDto> reviseApp(@RequestBody @Valid AppRevisionRequestDto appRevisionRequestDto);

    @DeleteMapping("/apps/{project-id}/{name}")
    ApiResponseDto<DeleteAppDeploymentResponseDto> deleteAppDeployment(
            @PathVariable("project-id") String projectId,
            @PathVariable String name
    );

    @PostMapping("/apps/{project-id}/{name}/secrets")
    ApiResponseDto<SecretCreationResponseDto> createSecret(
            @PathVariable("project-id") String projectId,
            @PathVariable("name") String appName,
            @RequestBody CreateSecretRequestDto request
    );

}
