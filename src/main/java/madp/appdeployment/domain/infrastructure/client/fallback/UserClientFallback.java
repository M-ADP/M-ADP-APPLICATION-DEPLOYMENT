package madp.appdeployment.domain.infrastructure.client.fallback;

import madp.appdeployment.domain.exception.UserServiceUnavailableException;
import madp.appdeployment.domain.infrastructure.client.UserClient;
import madp.appdeployment.domain.infrastructure.client.response.UserProfileResponseDto;
import org.springframework.stereotype.Component;

@Component
public class UserClientFallback implements UserClient {

    @Override
    public UserProfileResponseDto getUserProfile() {
        throw new UserServiceUnavailableException();
    }
}
