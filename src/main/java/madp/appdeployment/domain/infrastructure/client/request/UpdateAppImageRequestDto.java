package madp.appdeployment.domain.infrastructure.client.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record UpdateAppImageRequestDto(
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
            String image
    ) {}
}
