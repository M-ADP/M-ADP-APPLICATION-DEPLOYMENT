package madp.appdeployment.domain.infrastructure.github.client.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record WebhookCreatedResponse(
        @JsonProperty("id")
        Long id,
        
        @JsonProperty("active")
        Boolean active
) {}