package madp.appdeployment.global.presentation.response.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiResponseDto<T> {
    private final String message;
    private final T data;

    public static <T> ApiResponseDto<T> of(String message, T data) {
        return ApiResponseDto.<T>builder()
                .message(message)
                .data(data)
                .build();
    }
}
