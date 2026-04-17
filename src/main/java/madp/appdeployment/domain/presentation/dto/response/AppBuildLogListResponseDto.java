package madp.appdeployment.domain.presentation.dto.response;

import java.util.List;

public record AppBuildLogListResponseDto(
        String app_id,
        List<AppBuildResponse> builds
) {
    public record AppBuildResponse(
            Integer number,
            String result,
            Long timestamp,
            Long duration
    ) {}
}
