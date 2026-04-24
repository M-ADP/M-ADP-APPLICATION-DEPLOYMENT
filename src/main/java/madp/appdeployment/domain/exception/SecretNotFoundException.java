package madp.appdeployment.domain.exception;

import madp.appdeployment.global.exception.resource.ResourceNotFoundException;

public class SecretNotFoundException extends ResourceNotFoundException {
    public SecretNotFoundException() {
        super("존재하지 않는 Secret 이름입니다.");
    }
}