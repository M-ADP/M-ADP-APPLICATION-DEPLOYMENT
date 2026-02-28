package madp.appdeployment.global.infrastructure.feign.exception;

import madp.appdeployment.global.exception.MadpBusinessException;
import org.springframework.http.HttpStatus;

public class FeignClientUnauthorizedException extends MadpBusinessException {
  public FeignClientUnauthorizedException() {
    super("외부 서비스 인증에 실패했습니다.", HttpStatus.UNAUTHORIZED);
  }
}
