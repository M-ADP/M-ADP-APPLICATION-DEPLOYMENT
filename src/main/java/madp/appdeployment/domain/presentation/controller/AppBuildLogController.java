package madp.appdeployment.domain.presentation.controller;

import lombok.RequiredArgsConstructor;
import madp.appdeployment.domain.application.service.JenkinsService;
import madp.appdeployment.domain.presentation.dto.response.AppBuildLogDetailResponseDto;
import madp.appdeployment.domain.presentation.dto.response.AppBuildLogListResponseDto;
import madp.appdeployment.global.presentation.dto.response.LegacyApiResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/apps")
@RequiredArgsConstructor
public class AppBuildLogController {
    private final JenkinsService jenkinsService;

    @GetMapping("/{project_id}/{name}/build-logs")
    public ResponseEntity<LegacyApiResponseDto<AppBuildLogListResponseDto>> getBuildLogs(
            @PathVariable("project_id") String projectId,
            @PathVariable("name") String name
    ) {
        return ResponseEntity.ok(
                LegacyApiResponseDto.of(
                        "App deployment build logs retrieved successfully",
                        jenkinsService.getBuildLogs(projectId, name)
                )
        );
    }

    @GetMapping("/{project_id}/{name}/build-logs/{build_number}")
    public ResponseEntity<LegacyApiResponseDto<AppBuildLogDetailResponseDto>> getBuildLogDetail(
            @PathVariable("project_id") String projectId,
            @PathVariable("name") String name,
            @PathVariable("build_number") Integer buildNumber
    ) {
        return ResponseEntity.ok(
                LegacyApiResponseDto.of(
                        "App deployment build log detail retrieved successfully",
                        jenkinsService.getBuildLogDetail(projectId, name, buildNumber)
                )
        );
    }
}
