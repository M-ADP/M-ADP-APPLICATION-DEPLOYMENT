package madp.appdeployment.domain.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record JenkinsCallBackRequestDto(
        @JsonProperty("repository_id")
        @NotNull(message = "Repository 아이디는 null일 수 없습니다.")
        Long repositoryId
) {}
