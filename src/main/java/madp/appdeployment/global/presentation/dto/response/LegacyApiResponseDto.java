package madp.appdeployment.global.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record LegacyApiResponseDto<T>(
    @JsonProperty("success")
    boolean success,

    @JsonProperty("message")
    String message,

    @JsonProperty("data")
    T data
) {
    public static <T> LegacyApiResponseDto<T> of(String message, T data) {
        return LegacyApiResponseDto.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }
}
