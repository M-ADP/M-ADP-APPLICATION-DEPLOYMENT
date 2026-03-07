package madp.appdeployment.global.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record ApiResponseDto<T>(
    @JsonProperty("message")
    String message,
    
    @JsonProperty("data")
    T data
) {

    public static <T> ApiResponseDto<T> of(String message, T data) {
        return ApiResponseDto.<T>builder()
                .message(message)
                .data(data)
                .build();
    }
}
