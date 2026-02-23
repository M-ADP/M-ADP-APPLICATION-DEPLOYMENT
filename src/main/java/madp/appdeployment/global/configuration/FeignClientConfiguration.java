package madp.appdeployment.global.configuration;

import feign.Logger;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import madp.appdeployment.global.infrastructure.feign.FeignClientErrorDecoder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "madp.appdeployment")
public class FeignClientConfiguration {
    @Bean
    public ErrorDecoder errorDecoder() {
        return new FeignClientErrorDecoder();
    }

    @Bean
    public Retryer retryer() {
        return new Retryer.Default();
    }

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

}