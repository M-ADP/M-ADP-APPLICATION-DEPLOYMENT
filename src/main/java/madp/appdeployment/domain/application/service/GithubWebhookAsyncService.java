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
        log.info("[processWebhook] 수신 - eventType={}", eventType);

        try {
            if("push".equals(eventType)) {
                GithubWebhookPushRequestDto githubWebhookPushRequestDto = objectMapper.readValue(content, GithubWebhookPushRequestDto.class);
                log.info("[processWebhook] push 이벤트 처리 - repositoryId={}, branch={}",
                        githubWebhookPushRequestDto.repository().id(), githubWebhookPushRequestDto.getBranchName());
                githubWebhookService.handlePushEvent(githubWebhookPushRequestDto);
                log.info("[processWebhook] push 이벤트 처리 완료 - repositoryId={}", githubWebhookPushRequestDto.repository().id());
            }
            else if("organization".equals(eventType)) {
                JsonNode payload = objectMapper.readTree(content);
                String action = payload.get("action").asString();
                log.info("[processWebhook] organization 이벤트 처리 - action={}", action);

                GithubWebhookOrganizationRequestDto githubWebhookOrganizationRequestDto = objectMapper.readValue(content, GithubWebhookOrganizationRequestDto.class);
                if("member_added".equals(action)) {
                    githubWebhookService.addMember(githubWebhookOrganizationRequestDto);
                    log.info("[processWebhook] organization 멤버 추가 완료 - organizationId={}, githubId={}",
                            githubWebhookOrganizationRequestDto.organizationId(), githubWebhookOrganizationRequestDto.githubId());
                }
                else if("member_removed".equals(action)) {
                    githubWebhookService.removeMember(githubWebhookOrganizationRequestDto);
                    log.info("[processWebhook] organization 멤버 제거 완료 - organizationId={}, githubId={}",
                            githubWebhookOrganizationRequestDto.organizationId(), githubWebhookOrganizationRequestDto.githubId());
                }
                else {
                    log.warn("[processWebhook] 처리되지 않은 organization action - action={}", action);
                }
            }
            else {
                JsonNode payload = objectMapper.readTree(content);
                String action = payload.get("action").asString();
                log.info("[processWebhook] installation 이벤트 처리 - eventType={}, action={}", eventType, action);

                GithubWebhookInstallationRequestDto githubWebhookInstallationRequestDto = objectMapper.readValue(content, GithubWebhookInstallationRequestDto.class);
                // "installation" 방어적 코드 NPE 방어
                if("installation".equals(eventType)) {
                    if("created".equals(action)) {
                        githubWebhookService.installGithubApp(githubWebhookInstallationRequestDto);
                        log.info("[processWebhook] GitHub App 설치 완료 - installationId={}",
                                githubWebhookInstallationRequestDto.installation().id());
                    }
                    else if("deleted".equals(action)) {
                        githubWebhookService.uninstallGithubApp(githubWebhookInstallationRequestDto.installation().id());
                        log.info("[processWebhook] GitHub App 제거 완료 - installationId={}",
                                githubWebhookInstallationRequestDto.installation().id());
                    }
                    else {
                        log.warn("[processWebhook] 처리되지 않은 installation action - action={}", action);
                    }
                }
                // "installation_repositories" 방어적 코드 NPE 방어
                else if("installation_repositories".equals(eventType)) {
                    githubWebhookService.updateRepositories(githubWebhookInstallationRequestDto);
                    log.info("[processWebhook] installation_repositories 업데이트 완료 - installationId={}",
                            githubWebhookInstallationRequestDto.installation().id());
                }
                else {
                    log.warn("[processWebhook] 처리되지 않은 eventType - eventType={}, action={}", eventType, action);
                }
            }
        }
        catch (Exception e) {
            // 추후 SSE를 통해, Github Webhook에 에러가 발생했다는 사실을 공지 (현재는 로깅만)
            log.error("[processWebhook] 처리 중 오류 발생 - eventType={}, error={}", eventType, e.getMessage(), e);
        }
    }
}
