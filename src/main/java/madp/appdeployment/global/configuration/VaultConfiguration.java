package madp.appdeployment.global.configuration;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VaultConfiguration {
    static {
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
