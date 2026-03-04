package madp.appdeployment.domain.infrastructure.client;

import madp.appdeployment.domain.infrastructure.client.fallback.UserClientFallback;
import madp.appdeployment.domain.infrastructure.client.response.UserProfileResponseDto;
import madp.appdeployment.global.configuration.InternalServiceCommunicationConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        name = "user-client",
        fallback = UserClientFallback.class,
        configuration = InternalServiceCommunicationConfiguration.class
)
public interface UserClient {
    @GetMapping("/user/profile")
    UserProfileResponseDto getUserProfile();
}
