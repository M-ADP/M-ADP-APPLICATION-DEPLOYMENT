package madp.appdeployment.domain.infrastructure.client;

import madp.appdeployment.domain.infrastructure.client.fallback.MonitoringClientFallback;
import madp.appdeployment.domain.infrastructure.client.request.TrafficRangeRequestDto;
import madp.appdeployment.domain.infrastructure.client.response.NetworkMetricsResponseDto;
import madp.appdeployment.domain.infrastructure.client.response.ResourceMetricsResponseDto;
import madp.appdeployment.domain.infrastructure.client.response.UserMetricsResponseDto;
import madp.appdeployment.global.configuration.InternalServiceCommunicationConfiguration;
import madp.appdeployment.global.presentation.dto.response.ApiResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "monitoring-client",
        fallback = MonitoringClientFallback.class,
        configuration = InternalServiceCommunicationConfiguration.class
)
public interface MonitoringClient {

    @GetMapping("/monitoring/app-deployment/{project_id}/{app_deployment_id}/users")
    ApiResponseDto<UserMetricsResponseDto> getUserMetrics(
            @PathVariable("project_id") String projectId,
            @PathVariable("app_deployment_id") String appDeploymentId
    );

    @GetMapping("/monitoring/app-deployment/{project_id}/{app_deployment_id}/traffic")
    ApiResponseDto<NetworkMetricsResponseDto> getTrafficMetrics(
            @PathVariable("project_id") String projectId,
            @PathVariable("app_deployment_id") String appDeploymentId,
            @SpringQueryMap TrafficRangeRequestDto rangeRequest
    );

    @GetMapping("/monitoring/app-deployment/{project_id}/{app_deployment_id}/resource")
    ApiResponseDto<ResourceMetricsResponseDto> getResourceMetrics(
            @PathVariable("project_id") String projectId,
            @PathVariable("app_deployment_id") String appDeploymentId,
            @SpringQueryMap TrafficRangeRequestDto rangeRequest
    );
}
