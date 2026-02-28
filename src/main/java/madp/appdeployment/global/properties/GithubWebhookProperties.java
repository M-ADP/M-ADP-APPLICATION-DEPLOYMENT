package madp.appdeployment.global.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "github.webhook")
@RequiredArgsConstructor
public class GithubWebhookProperties {
    private final String privateKey;
    private final String webhookSecret;
    private final String appId;
}
