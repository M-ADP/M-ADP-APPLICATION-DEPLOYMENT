package madp.appdeployment.domain.infrastructure.client.fallback;

import madp.appdeployment.domain.exception.ProjectServiceUnavailableException;
import madp.appdeployment.domain.infrastructure.client.ResourceClient;
import madp.appdeployment.domain.infrastructure.client.request.AppDeploymentRequestDto;
import madp.appdeployment.domain.infrastructure.client.request.AppRevisionRequestDto;
import madp.appdeployment.domain.infrastructure.client.request.CreateSecretRequestDto;
import madp.appdeployment.domain.infrastructure.client.response.AppDeploymentResourceStatusResponseDto;
import madp.appdeployment.domain.infrastructure.client.response.AppRevisionResponseDto;
import madp.appdeployment.domain.infrastructure.client.response.DeleteAppDeploymentResponseDto;
import madp.appdeployment.domain.infrastructure.client.response.PodLogsResponseDto;
import madp.appdeployment.domain.infrastructure.client.response.SecretCreationResponseDto;
import madp.appdeployment.domain.presentation.dto.response.AppBuildLogDetailResponseDto;
import madp.appdeployment.domain.presentation.dto.response.AppBuildLogListResponseDto;
import madp.appdeployment.global.presentation.dto.response.ApiResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ResourceClientFallback implements ResourceClient {

    @Override
    public void createAppDeployment(String projectId, AppDeploymentRequestDto appDeploymentRequestDto) {
        throw new ProjectServiceUnavailableException();
    }

    @Override
    public ApiResponseDto<List<AppDeploymentResourceStatusResponseDto.AppResourceDto>> getAppDeploymentResourceStatus(String projectId, List<String> appNames) {
        throw new ProjectServiceUnavailableException();
    }

    @Override
    public ApiResponseDto<PodLogsResponseDto.LogDataDto> getPodLogs(String projectId, String appName) {
        throw new ProjectServiceUnavailableException();
    }

    @Override
    public ApiResponseDto<AppRevisionResponseDto> reviseApp(AppRevisionRequestDto appRevisionRequestDto) {
        throw new ProjectServiceUnavailableException();
    }

    @Override
    public ApiResponseDto<DeleteAppDeploymentResponseDto> deleteAppDeployment(String projectId, String name) {
        throw new ProjectServiceUnavailableException();
    }

    @Override
    public ApiResponseDto<SecretCreationResponseDto> createSecret(String projectId, String appName, CreateSecretRequestDto request) {
        throw new ProjectServiceUnavailableException();
    }

}
