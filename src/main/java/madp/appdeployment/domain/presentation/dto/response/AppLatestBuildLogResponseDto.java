package madp.appdeployment.domain.presentation.dto.response;

public record AppLatestBuildLogResponseDto(
        String appId,
        Integer number,
        String result,
        Long timestamp,
        Long duration,
        String logs
) {}
