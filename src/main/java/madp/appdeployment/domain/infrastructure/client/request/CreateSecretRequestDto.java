package madp.appdeployment.domain.infrastructure.client.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public record CreateSecretRequestDto(
        @JsonProperty("data")
        Map<String, String> data
) {
}