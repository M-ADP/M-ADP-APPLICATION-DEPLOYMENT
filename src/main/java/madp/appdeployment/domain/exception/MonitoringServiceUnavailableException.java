package madp.appdeployment.domain.exception;

import madp.appdeployment.global.exception.service.ExternalServiceUnavailableException;

public class MonitoringServiceUnavailableException extends ExternalServiceUnavailableException {
    public MonitoringServiceUnavailableException() {
        super("모니터링");
    }
}
