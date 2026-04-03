package madp.appdeployment.global.infrastructure.feign;

import feign.Request;
import feign.Response;
import madp.appdeployment.global.infrastructure.feign.exception.FeignClientBadRequestException;
import madp.appdeployment.global.infrastructure.feign.exception.FeignClientServiceUnavailableException;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FeignClientErrorDecoderTest {

    private final FeignClientErrorDecoder errorDecoder = new FeignClientErrorDecoder();

    @Test
    void decodePreservesClientErrorStatusAndBody() {
        Response response = response(404, "Not Found", "{\"message\":\"app not found\"}");

        FeignClientBadRequestException exception = assertThrows(
                FeignClientBadRequestException.class,
                () -> errorDecoder.decode("ResourceClient#deleteAppDeployment", response)
        );

        assertEquals(404, exception.getUpstreamStatus());
        assertEquals("{\"message\":\"app not found\"}", exception.getUpstreamBody());
    }

    @Test
    void decodeMapsServerErrorsToServiceUnavailableException() {
        Response response = response(503, "Service Unavailable", "{\"message\":\"down\"}");

        assertThrows(
                FeignClientServiceUnavailableException.class,
                () -> errorDecoder.decode("ResourceClient#deleteAppDeployment", response)
        );
    }

    private Response response(int status, String reason, String body) {
        return Response.builder()
                .status(status)
                .reason(reason)
                .request(Request.create(
                        Request.HttpMethod.DELETE,
                        "/apps/123/api-server",
                        Map.of(),
                        null,
                        StandardCharsets.UTF_8,
                        null
                ))
                .headers(Map.of())
                .body(body, StandardCharsets.UTF_8)
                .build();
    }
}
