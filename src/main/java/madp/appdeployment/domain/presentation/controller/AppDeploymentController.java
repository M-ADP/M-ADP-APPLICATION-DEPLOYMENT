package madp.appdeployment.domain.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import madp.appdeployment.domain.application.service.AppDeploymentService;
import madp.appdeployment.domain.domain.vo.ResourceInfo;
import madp.appdeployment.domain.presentation.dto.request.CreateAppDeploymentRequestDto;
import madp.appdeployment.domain.presentation.dto.request.DeleteAppDeploymentRequestDto;
import madp.appdeployment.domain.presentation.dto.request.UpdateGithubInfoRequestDto;
import madp.appdeployment.domain.presentation.dto.request.UpdateResourceInfoRequestDto;
import madp.appdeployment.domain.presentation.dto.response.AppDeploymentInfoResponseDto;
import madp.appdeployment.domain.presentation.dto.response.AppDeploymentStatusResponseDto;
import madp.appdeployment.domain.presentation.dto.response.AppResourceStatusResponseDto;
import madp.appdeployment.global.presentation.response.dto.ApiResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/apps")
@RequiredArgsConstructor
public class AppDeploymentController {
    private final AppDeploymentService appDeploymentService;

    @PostMapping
    public ResponseEntity<Void> createAppDeployment(
            @Valid @RequestBody CreateAppDeploymentRequestDto createAppDeploymentRequestDto
    ) {
        appDeploymentService.createAppDeployment(createAppDeploymentRequestDto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/github")
    public ResponseEntity<Void> updateGithubInfo(
            @Valid @RequestBody UpdateGithubInfoRequestDto updateGithubInfoRequestDto
    ) {
        appDeploymentService.updateGithubInfo(updateGithubInfoRequestDto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/resources")
    public ResponseEntity<Void> updateResourceInfo(
            @Valid @RequestBody UpdateResourceInfoRequestDto updateResourceInfoRequestDto
    ) {
        appDeploymentService.updateAppDeploymentResourceInfo(
                updateResourceInfoRequestDto.appDeploymentId(),
                ResourceInfo.builder()
                        .cpu(updateResourceInfoRequestDto.cpu())
                        .memory(updateResourceInfoRequestDto.memory())
                        .disk(updateResourceInfoRequestDto.disk())
                        .build()
        );
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAppDeployment(
            @Valid @RequestBody DeleteAppDeploymentRequestDto deleteAppDeploymentRequestDto
    ) {
        appDeploymentService.deleteAppDeployment(deleteAppDeploymentRequestDto.appDeploymentId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<AppDeploymentStatusResponseDto>>> getAppDeploymentsByProjectId(
            @RequestParam(value = "project_id") String projectId
    ) {
        return ResponseEntity.ok(
                ApiResponseDto.of(
                        "앱 배포 목록 조회 성공",
                        appDeploymentService.getAppDeploymentsByProjectId(projectId)
                )
        );
    }

    @GetMapping("/logs")
    public ResponseEntity<ApiResponseDto<String>> getLogsByProjectIdAndAppId(
            @RequestParam(value = "project_id") String projectId,
            @RequestParam(value = "app_name") String appName
    ) {
        return ResponseEntity.ok(
                ApiResponseDto.of(
                        "앱 로그 조회 성공",
                        appDeploymentService.getLogs(projectId, appName)
                )
        );
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponseDto<AppResourceStatusResponseDto>> getAppDeploymentByProjectIdAndAppName(
            @RequestParam(value = "project_id") String projectId,
            @RequestParam(value = "app_name") String appName
    ) {
        return ResponseEntity.ok(
                ApiResponseDto.of(
                        "앱 리소스 상태 조회 성공",
                        appDeploymentService.getAppDeploymentByProjectIdAndAppName(projectId, appName)
                )
        );
    }

    @GetMapping("/details")
    public ResponseEntity<ApiResponseDto<AppDeploymentInfoResponseDto>> getDetailsProjectIdAndAppName(
            @RequestParam(value = "project_id") String projectId,
            @RequestParam(value = "app_name") String appName
    ) {
        return ResponseEntity.ok(
                ApiResponseDto.of(
                        "앱 리소스 세부사항 조회 성공",
                        appDeploymentService.getDetailsProjectIdAndAppName(projectId, appName)
                )
        );
    }
}
