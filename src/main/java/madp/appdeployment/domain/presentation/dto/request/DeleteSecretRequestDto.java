package madp.appdeployment.domain.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record DeleteSecretRequestDto(
        @JsonProperty("secret_names")
        @NotNull(message = "삭제할 Secret 이름 목록은 필수입니다.")
        @NotEmpty(message = "삭제할 Secret 이름 목록은 필수입니다.")
        List<String> secretNames
) {
}
