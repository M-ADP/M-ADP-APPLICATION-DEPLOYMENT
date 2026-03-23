package madp.appdeployment.domain.domain.repository.dto;

public record ProjectResourceUsageSumDto(
        Number totalCpu,
        Number totalMemory,
        Number totalDisk
) {
}
