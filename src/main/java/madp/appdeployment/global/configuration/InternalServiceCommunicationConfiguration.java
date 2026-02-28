package madp.appdeployment.global.configuration;

import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import madp.appdeployment.global.infrastructure.internal.InternalRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@RequiredArgsConstructor
@Import(FeignClientConfiguration.class)
public class InternalServiceCommunicationConfiguration {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new InternalRequestInterceptor();
    }
}
