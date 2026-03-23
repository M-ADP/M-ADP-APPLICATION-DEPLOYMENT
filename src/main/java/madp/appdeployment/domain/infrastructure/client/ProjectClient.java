package madp.appdeployment.domain.infrastructure.client;

import madp.appdeployment.domain.infrastructure.client.fallback.ProjectClientFallback;
import madp.appdeployment.domain.infrastructure.client.response.ProjectAvailableResponseDto;
import madp.appdeployment.domain.infrastructure.client.response.ProjectOwnerResponseDto;
import madp.appdeployment.domain.infrastructure.client.response.ProjectResourceLimitResponseDto;
import madp.appdeployment.global.configuration.InternalServiceCommunicationConfiguration;
import madp.appdeployment.global.presentation.dto.response.ApiResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "project-client",
        fallback = ProjectClientFallback.class,
        configuration = InternalServiceCommunicationConfiguration.class
)
public interface ProjectClient {
    @GetMapping("/projects/available")
    ApiResponseDto<ProjectAvailableResponseDto> getProjectAvailable(@RequestParam("project_id") String projectId);

    @GetMapping("/projects/owner")
    ApiResponseDto<ProjectOwnerResponseDto> getProjectOwner(@RequestParam("project_id") String projectId);

    @GetMapping("/projects/resource-limit/{project_id}")
    ApiResponseDto<ProjectResourceLimitResponseDto> getProjectResourceLimit(@PathVariable("project_id") String projectId);
}
