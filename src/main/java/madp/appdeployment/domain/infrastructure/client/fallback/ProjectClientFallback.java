package madp.appdeployment.domain.infrastructure.client.fallback;

import madp.appdeployment.domain.exception.ProjectServiceUnavailableException;
import madp.appdeployment.domain.infrastructure.client.ProjectClient;
import madp.appdeployment.domain.infrastructure.client.response.ProjectAvailableResponseDto;
import madp.appdeployment.domain.infrastructure.client.response.ProjectOwnerResponseDto;
import org.springframework.stereotype.Component;

@Component
public class ProjectClientFallback implements ProjectClient {

    @Override
    public ProjectAvailableResponseDto getProjectAvailable(String projectId) {
        throw new ProjectServiceUnavailableException();
    }

    @Override
    public ProjectOwnerResponseDto getProjectOwner(String projectId) {
        throw new ProjectServiceUnavailableException();
    }
}