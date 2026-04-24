package madp.appdeployment.domain.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record CreateSecretRequestDto(
        @JsonProperty("data")
        @NotNull(message = "Secret 데이터는 필수입니다.")
        @NotEmpty(message = "Secret 데이터는 필수입니다.")
        Map<String, String> data
) {
}
