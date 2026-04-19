package madp.appdeployment.domain.infrastructure.client.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record DeleteSecretRequestDto(
        @JsonProperty("secret_names")
        List<String> secretNames
) {
}