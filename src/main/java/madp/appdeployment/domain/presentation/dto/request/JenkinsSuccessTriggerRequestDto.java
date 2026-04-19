package madp.appdeployment.domain.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record JenkinsSuccessTriggerRequestDto(
        @JsonProperty("repository_id")
        @NotNull(message = "레포지토리 ID는 존재해야합니다.")
        @Positive(message = "레포지토리 ID는 양수여야 합니다.")
        Long repositoryId,

        @JsonProperty("tag")
        @NotNull(message = "태그는 존재해야합니다.")
        @NotBlank(message = "태그는 존재해야합니다.")
        String tag,

        @JsonProperty("port")
        @NotNull(message = "포트는 존재해야합니다.")
        @Positive(message = "포트는 양수여야 합니다.")
        Integer port
) {}