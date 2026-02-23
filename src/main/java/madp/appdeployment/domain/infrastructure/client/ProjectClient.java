package madp.appdeployment.domain.infrastructure.client;

import madp.appdeployment.domain.infrastructure.client.fallback.ProjectClientFallback;
import madp.appdeployment.domain.infrastructure.client.response.ProjectAvailableResponseDto;
import madp.appdeployment.global.configuration.InternalServiceCommunicationConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "project-client",
        url = "http://localhost:8082/v1", // 나중에 배포 주소로 바꿀 예정
        fallback = ProjectClientFallback.class,
        configuration = InternalServiceCommunicationConfiguration.class
)
public interface ProjectClient {
    @GetMapping("/projects/available")
    ProjectAvailableResponseDto getProjectAvailable(@RequestParam("project_id") String projectId);
}