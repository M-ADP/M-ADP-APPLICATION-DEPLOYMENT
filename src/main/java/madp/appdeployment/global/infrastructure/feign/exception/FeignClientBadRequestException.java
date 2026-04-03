package madp.appdeployment.global.infrastructure.feign.exception;

import lombok.Getter;
import madp.appdeployment.global.exception.MadpBusinessException;
import org.springframework.http.HttpStatus;

@Getter
public class FeignClientBadRequestException extends MadpBusinessException {
    public FeignClientBadRequestException() {
        this(HttpStatus.BAD_REQUEST.value(), "");
    }

    private final int upstreamStatus;
    private final String upstreamBody;

    public FeignClientBadRequestException(int upstreamStatus, String upstreamBody) {
        super("외부 서비스가 요청을 거절했습니다. (status=" + upstreamStatus + ")", HttpStatus.BAD_REQUEST);
        this.upstreamStatus = upstreamStatus;
        this.upstreamBody = upstreamBody == null ? "" : upstreamBody;
    }

    public boolean isNotFound() {
        return upstreamStatus == HttpStatus.NOT_FOUND.value();
    }
}
