package madp.appdeployment.global.infrastructure.feign;

import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import madp.appdeployment.global.infrastructure.feign.exception.FeignClientBadRequestException;
import madp.appdeployment.global.infrastructure.feign.exception.FeignClientServiceUnavailableException;
import madp.appdeployment.global.infrastructure.feign.exception.FeignClientTimeoutException;
import madp.appdeployment.global.infrastructure.feign.exception.FeignClientUnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class FeignClientErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        int status = response.status();
        String responseBody = readResponseBody(response);

        if (status == HttpStatus.UNAUTHORIZED.value()) {
            throw new FeignClientUnauthorizedException();
        }
        else if (status == HttpStatus.REQUEST_TIMEOUT.value() || status == HttpStatus.GATEWAY_TIMEOUT.value()) {
            throw new FeignClientTimeoutException();
        }
        else if (isClientError(status)) {
            throw new FeignClientBadRequestException(status, responseBody);
        }
        else {
            throw new FeignClientServiceUnavailableException();
        }
    }

    private boolean isClientError(int status) {
        return status >= 400 && status < 500;
    }

    private String readResponseBody(Response response) {
        if (response.body() == null) {
            return "";
        }

        try (var reader = response.body().asReader(StandardCharsets.UTF_8)) {
            return Util.toString(reader);
        } catch (IOException e) {
            return "[response body unavailable: " + e.getMessage() + "]";
        }
    }
}
