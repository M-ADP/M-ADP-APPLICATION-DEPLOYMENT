package madp.appdeployment.domain.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import madp.appdeployment.domain.presentation.dto.request.GithubWebhookInstallationRequestDto;
import madp.appdeployment.domain.presentation.dto.request.GithubWebhookOrganizationRequestDto;
import madp.appdeployment.domain.presentation.dto.request.GithubWebhookPushRequestDto;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class GithubWebhookAsyncService {
    private final GithubWebhookService githubWebhookService;
    private final ObjectMapper objectMapper;

    @Async("githubWebhookTaskExecutor")
    public void processWebhook(String eventType, String content) {
        try {
            if("push".equals(eventType)) {
                GithubWebhookPushRequestDto githubWebhookPushRequestDto = objectMapper.readValue(content, GithubWebhookPushRequestDto.class);
                githubWebhookService.handlePushEvent(githubWebhookPushRequestDto);
            }
            else if("organization".equals(eventType)) {
                JsonNode payload = objectMapper.readTree(content);
                String action = payload.get("action").asString();
                
                GithubWebhookOrganizationRequestDto githubWebhookOrganizationRequestDto = objectMapper.readValue(content, GithubWebhookOrganizationRequestDto.class);
                if("member_added".equals(action)) {
                    githubWebhookService.addMember(githubWebhookOrganizationRequestDto);
                }
                else if("member_removed".equals(action)) {
                    githubWebhookService.removeMember(githubWebhookOrganizationRequestDto);
                }
            }
            else {
                JsonNode payload = objectMapper.readTree(content);
                String action = payload.get("action").asString();
                
                GithubWebhookInstallationRequestDto githubWebhookInstallationRequestDto = objectMapper.readValue(content, GithubWebhookInstallationRequestDto.class);
                // "installation" 방어적 코드 NPE 방어
                if("installation".equals(eventType)) {
                    if("created".equals(action)) {
                        githubWebhookService.installGithubApp(githubWebhookInstallationRequestDto);
                    }
                    else if("deleted".equals(action)) {
                        githubWebhookService.uninstallGithubApp(githubWebhookInstallationRequestDto.installation().id());
                    }
                }
                // "installation_repositories" 방어적 코드 NPE 방어
                else if("installation_repositories".equals(eventType)) {
                    githubWebhookService.updateRepositories(githubWebhookInstallationRequestDto);
                }
            }
        }
        catch (Exception e) {
            // 추후 SSE를 통해, Github Webhook에 에러가 발생했다는 사실을 공지 (현재는 로깅만)
            log.error(e.getMessage());
        }
    }
}
