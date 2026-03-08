package madp.appdeployment.global.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "jenkins")
public class JenkinsProperties {
    private final String username;
    private final String apiKey;
}
