package madp.appdeployment.domain.presentation.controller;

import lombok.RequiredArgsConstructor;
import madp.appdeployment.domain.application.service.AppDeploymentMonitoringService;
import madp.appdeployment.domain.infrastructure.client.request.TrafficRangeRequestDto;
import madp.appdeployment.domain.infrastructure.client.response.NetworkMetricsResponseDto;
import madp.appdeployment.domain.infrastructure.client.response.ResourceMetricsResponseDto;
import madp.appdeployment.domain.infrastructure.client.response.UserMetricsResponseDto;
import madp.appdeployment.global.presentation.dto.response.ApiResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/apps/{project_id}/{app_deployment_id}/monitoring")
@RequiredArgsConstructor
public class AppDeploymentMonitoringController {

    private final AppDeploymentMonitoringService appDeploymentMonitoringService;

    @GetMapping("/users")
    public ResponseEntity<ApiResponseDto<UserMetricsResponseDto>> getUserMetrics(
            @PathVariable("project_id") String projectId,
            @PathVariable("app_deployment_id") String appDeploymentId
    ) {
        return ResponseEntity.ok(
                ApiResponseDto.of(
                        "고유 사용자 조회 성공",
                        appDeploymentMonitoringService.getUserMetrics(projectId, appDeploymentId)
                )
        );
    }

    @GetMapping("/traffic")
    public ResponseEntity<ApiResponseDto<NetworkMetricsResponseDto>> getTrafficMetrics(
            @PathVariable("project_id") String projectId,
            @PathVariable("app_deployment_id") String appDeploymentId,
            @ModelAttribute TrafficRangeRequestDto rangeRequest
    ) {
        return ResponseEntity.ok(
                ApiResponseDto.of(
                        "트래픽 조회 성공",
                        appDeploymentMonitoringService.getTrafficMetrics(projectId, appDeploymentId, rangeRequest)
                )
        );
    }

    @GetMapping("/resource")
    public ResponseEntity<ApiResponseDto<ResourceMetricsResponseDto>> getResourceMetrics(
            @PathVariable("project_id") String projectId,
            @PathVariable("app_deployment_id") String appDeploymentId,
            @ModelAttribute TrafficRangeRequestDto rangeRequest
    ) {
        return ResponseEntity.ok(
                ApiResponseDto.of(
                        "리소스 조회 성공",
                        appDeploymentMonitoringService.getResourceMetrics(projectId, appDeploymentId, rangeRequest)
                )
        );
    }
}
