package madp.appdeployment.domain.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import madp.appdeployment.domain.application.service.AppDeploymentService;
import madp.appdeployment.domain.presentation.dto.request.CreateAppDeploymentRequestDto;
import madp.appdeployment.domain.presentation.dto.request.UpdateGithubInfoRequestDto;
import madp.appdeployment.domain.presentation.dto.response.AppDeploymentStatusResponseDto;
import madp.appdeployment.domain.presentation.dto.response.AppResourceStatusResponseDto;
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

    @GetMapping
    public ResponseEntity<List<AppDeploymentStatusResponseDto>> getAppDeploymentsByProjectId(
            @RequestParam(value = "project_id") String projectId
    ) {
        return ResponseEntity.ok(appDeploymentService.getAppDeploymentsByProjectId(projectId));
    }

    @GetMapping("/logs")
    public ResponseEntity<String> getLogsByProjectIdAndAppId(
            @RequestParam(value = "project_id") String projectId,
            @RequestParam(value = "app_name") String appName
    ) {
        return ResponseEntity.ok(appDeploymentService.getLogs(projectId, appName));
    }

    @GetMapping
    public ResponseEntity<AppResourceStatusResponseDto> getAppDeploymentByProjectIdAndAppName(
            @RequestParam(value = "project_id") String projectId,
            @RequestParam(value = "app_name") String appName
    ) {
        return ResponseEntity.ok(appDeploymentService.getAppDeploymentByProjectIdAndAppName(projectId, appName).getFirst());
    }
}
