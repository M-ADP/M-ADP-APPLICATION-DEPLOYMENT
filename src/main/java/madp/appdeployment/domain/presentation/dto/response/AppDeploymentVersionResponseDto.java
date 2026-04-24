package madp.appdeployment.domain.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record AppDeploymentVersionResponseDto(
        @JsonProperty("version")
        Integer version
) {}
