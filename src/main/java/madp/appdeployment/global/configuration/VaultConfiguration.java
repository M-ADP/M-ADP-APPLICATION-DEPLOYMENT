package madp.appdeployment.global.configuration;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VaultConfiguration {
    @PostConstruct
    public void init() {
        Dotenv dotenv = Dotenv.configure()
                .directory("/vault/secrets")
                .filename(".env")
                .ignoreIfMissing()
                .load();

        dotenv.entries().forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue())
        );
    }
}
