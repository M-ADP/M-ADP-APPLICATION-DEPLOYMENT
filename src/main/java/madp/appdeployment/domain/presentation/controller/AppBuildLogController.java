package madp.appdeployment.domain.presentation.controller;

import lombok.RequiredArgsConstructor;
import madp.appdeployment.domain.application.service.AppBuildLogService;
import madp.appdeployment.domain.presentation.dto.response.AppBuildLogDetailResponseDto;
import madp.appdeployment.domain.presentation.dto.response.AppBuildLogListResponseDto;
import madp.appdeployment.domain.presentation.dto.response.AppLatestBuildLogResponseDto;
import madp.appdeployment.global.presentation.dto.response.ApiResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/apps")
@RequiredArgsConstructor
public class AppBuildLogController {

    private final AppBuildLogService appBuildLogService;

    @GetMapping("/{project_id}/{name}/build-logs")
    public ResponseEntity<ApiResponseDto<AppBuildLogListResponseDto>> getBuildLogs(
            @PathVariable("project_id") String projectId,
            @PathVariable("name") String name
    ) {
        return ResponseEntity.ok(
                ApiResponseDto.of(
                        "App deployment build logs retrieved successfully",
                        appBuildLogService.getBuildLogs(projectId, name)
                )
        );
    }

    @GetMapping("/{project_id}/{name}/build-logs/latest")
    public ResponseEntity<ApiResponseDto<AppLatestBuildLogResponseDto>> getLatestBuildLog(
            @PathVariable("project_id") String projectId,
            @PathVariable("name") String name
    ) {
        return ResponseEntity.ok(
                ApiResponseDto.of(
                        "App deployment latest build log retrieved successfully",
                        appBuildLogService.getLatestBuildLog(projectId, name)
                )
        );
    }

    @GetMapping("/{project_id}/{name}/build-logs/{build_number}")
    public ResponseEntity<ApiResponseDto<AppBuildLogDetailResponseDto>> getBuildLogDetail(
            @PathVariable("project_id") String projectId,
            @PathVariable("name") String name,
            @PathVariable("build_number") Integer buildNumber
    ) {
        return ResponseEntity.ok(
                ApiResponseDto.of(
                        "App deployment build log detail retrieved successfully",
                        appBuildLogService.getBuildLogDetail(projectId, name, buildNumber)
                )
        );
    }
}
