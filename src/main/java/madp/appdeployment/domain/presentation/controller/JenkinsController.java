package madp.appdeployment.domain.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import madp.appdeployment.domain.application.service.JenkinsService;
import madp.appdeployment.domain.presentation.dto.request.JenkinsCallBackRequestDto;
import madp.appdeployment.domain.presentation.dto.request.JenkinsSuccessTriggerRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/apps/jenkins")
@RequiredArgsConstructor
public class JenkinsController {
    private final JenkinsService jenkinsService;

    @PostMapping("/callback/success")
    public ResponseEntity<Void> callBackSuccess(@RequestBody @Valid JenkinsSuccessTriggerRequestDto jenkinsSuccessTriggerRequestDto) {
        jenkinsService.successTrigger(jenkinsSuccessTriggerRequestDto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/callback/failure")
    public ResponseEntity<Void> callBackFailure(@RequestBody @Valid JenkinsCallBackRequestDto jenkinsCallBackRequestDto) {
        jenkinsService.failTrigger(jenkinsCallBackRequestDto.repositoryId());
        return ResponseEntity.noContent().build();
    }
}
