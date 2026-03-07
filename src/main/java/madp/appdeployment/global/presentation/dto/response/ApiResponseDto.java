package madp.appdeployment.global.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiResponseDto<T> {
    @JsonProperty("message")
    private final String message;
    
    @JsonProperty("data")
    private final T data;

    public static <T> ApiResponseDto<T> of(String message, T data) {
        return ApiResponseDto.<T>builder()
                .message(message)
                .data(data)
                .build();
    }
}
