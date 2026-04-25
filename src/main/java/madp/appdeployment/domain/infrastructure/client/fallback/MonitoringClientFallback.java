package madp.appdeployment.domain.infrastructure.client.fallback;

import madp.appdeployment.domain.exception.MonitoringServiceUnavailableException;
import madp.appdeployment.domain.infrastructure.client.MonitoringClient;
import madp.appdeployment.domain.infrastructure.client.request.TrafficRangeRequestDto;
import madp.appdeployment.domain.infrastructure.client.response.NetworkMetricsResponseDto;
import madp.appdeployment.domain.infrastructure.client.response.ResourceMetricsResponseDto;
import madp.appdeployment.domain.infrastructure.client.response.UserMetricsResponseDto;
import madp.appdeployment.global.presentation.dto.response.ApiResponseDto;
import org.springframework.stereotype.Component;

@Component
public class MonitoringClientFallback implements MonitoringClient {

    @Override
    public ApiResponseDto<UserMetricsResponseDto> getUserMetrics(String projectId, String appDeploymentId) {
        throw new MonitoringServiceUnavailableException();
    }

    @Override
    public ApiResponseDto<NetworkMetricsResponseDto> getTrafficMetrics(String projectId, String appDeploymentId, TrafficRangeRequestDto rangeRequest) {
        throw new MonitoringServiceUnavailableException();
    }

    @Override
    public ApiResponseDto<ResourceMetricsResponseDto> getResourceMetrics(String projectId, String appDeploymentId, TrafficRangeRequestDto rangeRequest) {
        throw new MonitoringServiceUnavailableException();
    }
}
