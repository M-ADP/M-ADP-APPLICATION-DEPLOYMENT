package madp.appdeployment.domain.exception;

import madp.appdeployment.global.exception.MadpBusinessException;
import org.springframework.http.HttpStatus;

public class GithubPrivateKeyInvalidException extends MadpBusinessException {
    public GithubPrivateKeyInvalidException() {
        super("Private Key 생성과정 중 오류가 발생했습니다.", HttpStatus.BAD_REQUEST);
    }
}
