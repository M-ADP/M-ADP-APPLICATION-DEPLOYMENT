package madp.appdeployment.domain.infrastructure.client.fallback;

import madp.appdeployment.domain.exception.ProjectServiceUnavailableException;
import madp.appdeployment.domain.infrastructure.client.ProjectClient;
import madp.appdeployment.domain.infrastructure.client.response.ProjectAvailableResponseDto;
import madp.appdeployment.domain.infrastructure.client.response.ProjectOwnerResponseDto;
import madp.appdeployment.domain.infrastructure.client.response.ProjectResourceLimitResponseDto;
import madp.appdeployment.global.presentation.dto.response.ApiResponseDto;
import org.springframework.stereotype.Component;

@Component
public class ProjectClientFallback implements ProjectClient {

    @Override
    public ApiResponseDto<ProjectAvailableResponseDto> getProjectAvailable(String projectId) {
        throw new ProjectServiceUnavailableException();
    }

    @Override
    public ApiResponseDto<ProjectOwnerResponseDto> getProjectOwner(String projectId) {
        throw new ProjectServiceUnavailableException();
    }

    @Override
    public ApiResponseDto<ProjectResourceLimitResponseDto> getProjectResourceLimit(String projectId) {
        throw new ProjectServiceUnavailableException();
    }
}
