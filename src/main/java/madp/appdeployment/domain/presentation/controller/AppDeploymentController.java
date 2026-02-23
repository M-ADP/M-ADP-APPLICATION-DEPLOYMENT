package madp.appdeployment.domain.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import madp.appdeployment.domain.application.service.AppDeploymentService;
import madp.appdeployment.domain.presentation.dto.request.CreateAppDeploymentRequestDto;
import madp.appdeployment.domain.presentation.dto.request.UpdateGithubInfoRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/apps")
@RequiredArgsConstructor
public class AppDeploymentController {
    private final AppDeploymentService appDeploymentService;

    @PostMapping
    public ResponseEntity<Void> createAppDeployment(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody CreateAppDeploymentRequestDto createAppDeploymentRequestDto
    ) {
        appDeploymentService.createAppDeployment(createAppDeploymentRequestDto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/github")
    public ResponseEntity<Void> updateGithubInfo(
            // @AuthenticationPrincipal Long userId,
            @Valid @RequestBody UpdateGithubInfoRequestDto updateGithubInfoRequestDto
    ) {
        appDeploymentService.updateGithubInfo(updateGithubInfoRequestDto);
        return ResponseEntity.noContent().build();
    }
}
