package madp.appdeployment.global.exception.service;

import madp.appdeployment.global.exception.MadpSystemError;
import org.springframework.http.HttpStatus;

public class ExternalServiceUnavailableException extends MadpSystemError {
    public ExternalServiceUnavailableException(String serviceName) {
        super(serviceName + " 서비스를 일시적으로 이용할 수 없습니다", HttpStatus.SERVICE_UNAVAILABLE);
    }
}
