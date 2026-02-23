package madp.appdeployment.domain.infrastructure.client.fallback;

import madp.appdeployment.domain.exception.ProjectServiceUnavailableException;
import madp.appdeployment.domain.infrastructure.client.ProjectClient;
import madp.appdeployment.domain.infrastructure.client.response.ProjectAvailableResponseDto;
import org.springframework.stereotype.Component;

@Component
public class ProjectClientFallback implements ProjectClient {

    @Override
    public ProjectAvailableResponseDto getProjectAvailable(String projectId) {
        throw new ProjectServiceUnavailableException();
    }
}