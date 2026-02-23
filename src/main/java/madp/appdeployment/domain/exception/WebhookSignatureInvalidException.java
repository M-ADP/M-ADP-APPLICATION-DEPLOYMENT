package madp.appdeployment.domain.exception;

import madp.appdeployment.global.exception.MadpBusinessException;
import org.springframework.http.HttpStatus;

public class WebhookSignatureInvalidException extends MadpBusinessException {
    public WebhookSignatureInvalidException() {
        super("Github Webhook Signature가 유효하지 못합니다.", HttpStatus.BAD_REQUEST);
    }
}
