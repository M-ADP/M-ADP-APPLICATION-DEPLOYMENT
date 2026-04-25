package madp.appdeployment.domain.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import madp.appdeployment.domain.infrastructure.client.MonitoringClient;
import madp.appdeployment.domain.infrastructure.client.request.TrafficRangeRequestDto;
import madp.appdeployment.domain.infrastructure.client.response.NetworkMetricsResponseDto;
import madp.appdeployment.domain.infrastructure.client.response.ResourceMetricsResponseDto;
import madp.appdeployment.domain.infrastructure.client.response.UserMetricsResponseDto;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppDeploymentMonitoringService {

    private final MonitoringClient monitoringClient;

    public UserMetricsResponseDto getUserMetrics(String projectId, String appDeploymentId) {
        log.info("[getUserMetrics] 요청 - projectId={}, appDeploymentId={}", projectId, appDeploymentId);
        UserMetricsResponseDto result = monitoringClient.getUserMetrics(projectId, appDeploymentId).data();
        log.info("[getUserMetrics] 완료 - projectId={}, appDeploymentId={}", projectId, appDeploymentId);
        return result;
    }

    public NetworkMetricsResponseDto getTrafficMetrics(String projectId, String appDeploymentId, TrafficRangeRequestDto rangeRequest) {
        log.info("[getTrafficMetrics] 요청 - projectId={}, appDeploymentId={}, start={}, end={}", projectId, appDeploymentId, rangeRequest.start(), rangeRequest.end());
        NetworkMetricsResponseDto result = monitoringClient.getTrafficMetrics(projectId, appDeploymentId, rangeRequest).data();
        log.info("[getTrafficMetrics] 완료 - projectId={}, appDeploymentId={}", projectId, appDeploymentId);
        return result;
    }

    public ResourceMetricsResponseDto getResourceMetrics(String projectId, String appDeploymentId, TrafficRangeRequestDto rangeRequest) {
        log.info("[getResourceMetrics] 요청 - projectId={}, appDeploymentId={}, start={}, end={}", projectId, appDeploymentId, rangeRequest.start(), rangeRequest.end());
        ResourceMetricsResponseDto result = monitoringClient.getResourceMetrics(projectId, appDeploymentId, rangeRequest).data();
        log.info("[getResourceMetrics] 완료 - projectId={}, appDeploymentId={}", projectId, appDeploymentId);
        return result;
    }
}
