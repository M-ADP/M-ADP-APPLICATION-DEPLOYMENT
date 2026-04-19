package madp.appdeployment.global.infrastructure.feign;

import feign.Response;
import feign.codec.ErrorDecoder;
import madp.appdeployment.global.infrastructure.feign.exception.FeignClientBadRequestException;
import madp.appdeployment.global.infrastructure.feign.exception.FeignClientNotFoundException;
import madp.appdeployment.global.infrastructure.feign.exception.FeignClientServiceUnavailableException;
import madp.appdeployment.global.infrastructure.feign.exception.FeignClientTimeoutException;
import madp.appdeployment.global.infrastructure.feign.exception.FeignClientUnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class FeignClientErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        int status = response.status();

        if (status == HttpStatus.NOT_FOUND.value()) {
            throw new FeignClientNotFoundException();
        }
        else if (status == HttpStatus.UNAUTHORIZED.value()) {
            throw new FeignClientUnauthorizedException();
        }
        else if (status == HttpStatus.REQUEST_TIMEOUT.value() || status == HttpStatus.GATEWAY_TIMEOUT.value()) {
            throw new FeignClientTimeoutException();
        }
        else if (isClientError(status)) {
            throw new FeignClientBadRequestException();
        }
        else {
            throw new FeignClientServiceUnavailableException();
        }
    }

    private boolean isClientError(int status) {
        return status >= 400 && status < 500;
    }
}
