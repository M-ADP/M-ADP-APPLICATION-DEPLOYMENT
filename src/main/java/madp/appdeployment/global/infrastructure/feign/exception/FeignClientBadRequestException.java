package madp.appdeployment.global.infrastructure.feign.exception;

import madp.appdeployment.global.exception.MadpBusinessException;
import org.springframework.http.HttpStatus;

public class FeignClientBadRequestException extends MadpBusinessException {
    public FeignClientBadRequestException() {
        super("요청한 정보를 처리할 수 없습니다.", HttpStatus.BAD_REQUEST);
    }
}
