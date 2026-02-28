package madp.appdeployment.domain.exception;

import madp.appdeployment.global.exception.MadpBusinessException;
import org.springframework.http.HttpStatus;

public class RequiredValueNullException extends MadpBusinessException {
    
    public RequiredValueNullException() {
        super("필수 값이 null입니다", HttpStatus.BAD_REQUEST);
    }
}