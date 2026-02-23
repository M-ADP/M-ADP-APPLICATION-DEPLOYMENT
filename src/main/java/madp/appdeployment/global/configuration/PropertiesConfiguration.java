package madp.appdeployment.global.configuration;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationPropertiesScan(basePackages = "madp.appdeployment.global.properties")
public class PropertiesConfiguration {
}