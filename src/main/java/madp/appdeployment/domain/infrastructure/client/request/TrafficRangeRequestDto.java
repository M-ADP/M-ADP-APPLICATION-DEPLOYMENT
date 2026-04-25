package madp.appdeployment.domain.infrastructure.client.request;

public record TrafficRangeRequestDto(
        String start,
        String end
) {}
