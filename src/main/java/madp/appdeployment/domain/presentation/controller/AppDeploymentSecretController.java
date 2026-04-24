package madp.appdeployment.domain.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import madp.appdeployment.domain.application.service.AppDeploymentSecretService;
import madp.appdeployment.domain.presentation.dto.request.CreateSecretRequestDto;
import madp.appdeployment.domain.presentation.dto.request.DeleteSecretRequestDto;
import madp.appdeployment.domain.presentation.dto.response.CreateSecretResponseDto;
import madp.appdeployment.global.presentation.dto.response.ApiResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/apps")
@RequiredArgsConstructor
public class AppDeploymentSecretController {

    private final AppDeploymentSecretService appDeploymentSecretService;

    @PostMapping("/{project-id}/{name}/secrets")
    public ResponseEntity<ApiResponseDto<CreateSecretResponseDto>> createSecret(
            @PathVariable("project-id") String projectId,
            @PathVariable("name") String appName,
            @Valid @RequestBody CreateSecretRequestDto requestDto
    ) {
        CreateSecretResponseDto response = appDeploymentSecretService.createSecret(
                projectId, appName, requestDto.data()
        );
        return ResponseEntity.ok(ApiResponseDto.of("Secret이 생성되었습니다.", response));
    }

    @DeleteMapping("/{project-id}/{name}/secrets")
    public ResponseEntity<Void> deleteSecret(
            @PathVariable("project-id") String projectId,
            @PathVariable("name") String appName,
            @Valid @RequestBody DeleteSecretRequestDto requestDto
    ) {
        appDeploymentSecretService.deleteSecret(projectId, appName, requestDto.secretNames());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{project-id}/{name}/secrets")
    public ResponseEntity<ApiResponseDto<List<String>>> getSecretNames(
            @PathVariable("project-id") String projectId,
            @PathVariable("name") String appName
    ) {
        List<String> names = appDeploymentSecretService.getSecretNames(projectId, appName);
        return ResponseEntity.ok(ApiResponseDto.of("Secret 목록 조회 성공", names));
    }
}
