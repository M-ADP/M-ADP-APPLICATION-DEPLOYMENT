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
//
//    @Async
//    public void processWebhookAsync(String githubEvent, String signature, String payload) {
//        switch (githubEvent) {
//            case "installation":
//               handleInstallationEvent(signature, payload);
//               break;
//            case "push":
//                handlePushEvent(signature, payload);
//                break;
//            default:
//                log.info("Unhandled GitHub event: {}", githubEvent);
//        }
//    }
//
//    private void handleInstallationEvent(String signature, String payload) {
//        log.info("Processing installation event");
//        // TODO: installation webhook 처리 로직 구현
//        // 1. 서명 검증 (GitHub App webhook secret 사용)
//        // 2. 이벤트 타입 확인 (created/deleted/repositories)
//        // 3. GitHubInstallationEntity 저장/업데이트/삭제
//    }
//
//    private void handlePushEvent(String signature, String payload) {
//        log.info("Processing push event");
//        // TODO: push webhook 처리 로직 구현
//        // 1. payload에서 repository 정보 파싱
//        // 2. repository로 AppDeployment 조회
//        // 3. 서명 검증 (해당 deployment의 webhookSecret 사용)
//        // 4. Branch 필터링
//        // 5. 상태 체크 (BUILDING 중복 방지)
//        // 6. Jenkins 트리거
//    }
}
