package madp.appdeployment.domain.infrastructure.client;

import madp.appdeployment.domain.infrastructure.client.fallback.UserClientFallback;
import madp.appdeployment.domain.infrastructure.client.response.UserProfileResponseDto;
import madp.appdeployment.global.configuration.InternalServiceCommunicationConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        name = "user-client",
        url = "http://localhost:8081/v1", // 나중에 배포 주소로 바꿀 예정
        fallback = UserClientFallback.class,
        configuration = InternalServiceCommunicationConfiguration.class
)
public interface UserClient {
    @GetMapping("/user/profile")
    UserProfileResponseDto getUserProfile();
}
