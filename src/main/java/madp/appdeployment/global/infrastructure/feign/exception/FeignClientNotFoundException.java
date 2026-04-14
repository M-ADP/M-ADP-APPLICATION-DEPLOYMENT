package madp.appdeployment.global.infrastructure.feign.exception;

import madp.appdeployment.global.exception.MadpBusinessException;
import org.springframework.http.HttpStatus;

public class FeignClientNotFoundException extends MadpBusinessException {
    public FeignClientNotFoundException() {
        super("요청한 리소스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
    }
}
