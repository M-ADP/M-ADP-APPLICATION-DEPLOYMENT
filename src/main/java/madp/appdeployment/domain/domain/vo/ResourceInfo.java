package madp.appdeployment.domain.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import madp.appdeployment.domain.exception.InvalidResourceInfoException;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResourceInfo {
    @Column(name = "resource_cpu", nullable = false)
    private Double cpu;

    @Column(name = "resource_memory", nullable = false)
    private Integer memory;

    @Column(name = "resource_disk", nullable = false)
    private Integer disk;

    @Builder
    public ResourceInfo(Double cpu, Integer memory, Integer disk) {
        validateFields(cpu, memory, disk);

        this.cpu = cpu;
        this.memory = memory;
        this.disk = disk;
    }

    // ConfigurationProperties로 정책에 따라서 validate 함수 바꿀 예정
    private void validateFields(Double cpu, Integer memory, Integer disk) {
        if (cpu == null) {
            throw new InvalidResourceInfoException("CPU는 필수입니다.");
        }
        
        if (cpu <= 0) {
            throw new InvalidResourceInfoException("CPU는 0보다 커야 합니다.");
        }
        
        if (memory == null) {
            throw new InvalidResourceInfoException("메모리는 필수입니다.");
        }
        
        if (memory <= 0) {
            throw new InvalidResourceInfoException("메모리는 0보다 커야 합니다.");
        }
        
        if (disk == null) {
            throw new InvalidResourceInfoException("디스크는 필수입니다.");
        }
        
        if (disk <= 0) {
            throw new InvalidResourceInfoException("디스크는 0보다 커야 합니다.");
        }
    }
}
