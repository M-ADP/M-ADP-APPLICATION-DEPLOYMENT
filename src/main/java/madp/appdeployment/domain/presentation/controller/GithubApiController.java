package madp.appdeployment.domain.presentation.controller;

import lombok.RequiredArgsConstructor;
import madp.appdeployment.domain.application.service.GithubWebhookAsyncService;
import madp.appdeployment.domain.application.service.GithubWebhookService;
import madp.appdeployment.domain.presentation.dto.response.GithubAllowedRepositoryResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/github")
@RequiredArgsConstructor
public class GithubApiController {
    private final GithubWebhookAsyncService githubWebhookAsyncService;
    private final GithubWebhookService githubWebhookService;

    /**
     * GitHub App webhook 수신 (Installation 이벤트만)
     * 
     * GitHub App 설치/제거 이벤트만 처리
     * installation, installation_repositories 이벤트 수신
     * 
     * 즉시 200 응답 후 비동기로 처리 (GitHub 타임아웃 10초 대응)
     */
    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(
            @RequestHeader("X-GitHub-Event") String githubEvent,
            @RequestBody String content
    ) {
        githubWebhookAsyncService.processWebhook(githubEvent, content);

        return ResponseEntity.noContent().build();
    }

    /**
     * 허용된 Repository 목록 조회
     * 
     * 사용자가 접근 가능한 GitHub organization의 허용된 repository 목록 반환
     * 프론트에서 GitHub 연동 시 repository 선택 UI에 사용
     */
    @GetMapping("/allowed-repositories")
    public ResponseEntity<List<GithubAllowedRepositoryResponseDto>> getAllowedRepositories() {
        return ResponseEntity.ok(githubWebhookService.getAllowedRepositories());
        // TODO: 구현 필요
        // 1. GitHub organization 멤버십 확인
        // 2. 해당 organization의 허용된 repository 목록 반환
        // 3. Installation 없으면 GitHub App 설치 안내

    }
}
