package madp.appdeployment.domain.application.support;

import lombok.RequiredArgsConstructor;
import madp.appdeployment.domain.exception.InvalidResourceInfoException;
import madp.appdeployment.global.properties.ResourceLimitProperties;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResourceLimitValidator {

    private final ResourceLimitProperties limits;

    public void validate(Double cpu, Double memory, Integer disk) {
        if (cpu < limits.minCpu() || cpu > limits.maxCpu()) {
            throw new InvalidResourceInfoException(
                    String.format("CPU는 %.1f 이상 %.1f 이하이어야 합니다.", limits.minCpu(), limits.maxCpu())
            );
        }
        if (memory < limits.minMemory() || memory > limits.maxMemory()) {
            throw new InvalidResourceInfoException(
                    String.format("메모리는 %.2fGB 이상 %.1fGB 이하이어야 합니다.", limits.minMemory(), limits.maxMemory())
            );
        }
        if (disk < limits.minDisk() || disk > limits.maxDisk()) {
            throw new InvalidResourceInfoException(
                    String.format("디스크 크기는 %dGB 이상 %dGB 이하이어야 합니다.", limits.minDisk(), limits.maxDisk())
            );
        }
    }
}
