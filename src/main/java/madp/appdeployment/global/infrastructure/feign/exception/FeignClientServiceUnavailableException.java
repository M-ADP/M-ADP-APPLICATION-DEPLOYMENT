package madp.appdeployment.global.infrastructure.feign.exception;

import madp.appdeployment.global.exception.MadpSystemError;
import org.springframework.http.HttpStatus;

public class FeignClientServiceUnavailableException extends MadpSystemError {
    public FeignClientServiceUnavailableException() {
        super("외부 서비스를 현재 사용할 수 없습니다.", HttpStatus.BAD_GATEWAY);
    }
}