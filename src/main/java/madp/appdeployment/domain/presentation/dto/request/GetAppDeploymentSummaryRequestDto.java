package madp.appdeployment.domain.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record GetAppDeploymentSummaryRequestDto(
        @JsonProperty("project_ids")
        @NotEmpty(message = "프로젝트 ID 목록은 존재해야합니다.")
        List<Long> projectIds
) {}