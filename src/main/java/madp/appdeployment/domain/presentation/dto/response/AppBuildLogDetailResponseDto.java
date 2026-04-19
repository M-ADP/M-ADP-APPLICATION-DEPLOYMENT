package madp.appdeployment.domain.presentation.dto.response;

public record AppBuildLogDetailResponseDto(
        Integer number,
        String logs
) {}
