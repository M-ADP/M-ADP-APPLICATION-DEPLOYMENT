package madp.appdeployment.domain.infrastructure.client.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record AppDeploymentRequestDto(
        @JsonProperty("name")
        @NotNull(message = "이름은 존재해야합니다.")
        @NotBlank(message = "이름은 존재해야합니다.")
        String name,

        @JsonProperty("containers")
        @NotNull(message = "컨테이너 정보는 존재해야합니다.")
        @Valid
        List<ContainerDto> containers
) {
    @Builder
    public record ContainerDto(
            @JsonProperty("name")
            @NotNull(message = "컨테이너 이름은 존재해야합니다.")
            @NotBlank(message = "컨테이너 이름은 존재해야합니다.")
            String name,

            @JsonProperty("image")
            @NotNull(message = "이미지는 존재해야합니다.")
            @NotBlank(message = "이미지는 존재해야합니다.")
            String image,

            @JsonProperty("ports")
            @NotNull(message = "포트 정보는 존재해야합니다.")
            List<Integer> ports,

            @JsonProperty("resources")
            @NotNull(message = "리소스 정보는 존재해야합니다.")
            @Valid
            ResourcesDto resources,

            @JsonProperty("disk")
            @Valid
            DiskDto disk
    ) {}

    @Builder
    public record ResourcesDto(
            @JsonProperty("limits")
            @NotNull(message = "제한 리소스는 존재해야합니다.")
            @Valid
            ResourceDto limits
    ) {}

    @Builder
    public record ResourceDto(
            @JsonProperty("cpu")
            @NotNull(message = "CPU는 존재해야합니다.")
            @NotBlank(message = "CPU는 존재해야합니다.")
            String cpu,

            @JsonProperty("memory")
            @NotNull(message = "메모리는 존재해야합니다.")
            @NotBlank(message = "메모리는 존재해야합니다.")
            String memory
    ) {}

    @Builder
    public record DiskDto(
            @JsonProperty("size")
            @NotNull(message = "디스크 크기는 존재해야합니다.")
            @NotBlank(message = "디스크 크기는 존재해야합니다.")
            String size
    ) {}
}