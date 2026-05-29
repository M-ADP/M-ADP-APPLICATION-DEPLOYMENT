package madp.appdeployment.global.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "resource.limit")
public record ResourceLimitProperties(
        @DefaultValue("4.0") double maxCpu,
        @DefaultValue("0.1") double minCpu,
        @DefaultValue("2.0") double maxMemory,
        @DefaultValue("0.25") double minMemory,
        @DefaultValue("50") int maxDisk,
        @DefaultValue("2") int minDisk
) {}
