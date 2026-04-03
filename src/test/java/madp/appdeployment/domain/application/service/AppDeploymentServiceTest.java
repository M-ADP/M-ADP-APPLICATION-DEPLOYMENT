package madp.appdeployment.domain.application.service;

import madp.appdeployment.domain.domain.entity.AppDeploymentEntity;
import madp.appdeployment.domain.domain.repository.AppDeploymentRepository;
import madp.appdeployment.domain.domain.repository.GithubAllowedRepoRepository;
import madp.appdeployment.domain.domain.vo.ResourceInfo;
import madp.appdeployment.domain.infrastructure.client.ProjectClient;
import madp.appdeployment.domain.infrastructure.client.ResourceClient;
import madp.appdeployment.global.infrastructure.feign.exception.FeignClientBadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppDeploymentServiceTest {

    @Mock
    private AppDeploymentRepository appDeploymentRepository;

    @Mock
    private GithubAllowedRepoRepository githubAllowedRepoRepository;

    @Mock
    private ProjectClient projectClient;

    @Mock
    private ResourceClient resourceClient;

    @InjectMocks
    private AppDeploymentService appDeploymentService;

    @Test
    void deleteAppDeploymentListByProjectIdDeletesAllProjectApps() {
        AppDeploymentEntity apiServer = AppDeploymentEntity.builder()
                .name("api-server")
                .projectId("123")
                .resourceInfo(resourceInfo())
                .port(8080)
                .build();
        AppDeploymentEntity worker = AppDeploymentEntity.builder()
                .name("worker")
                .projectId("123")
                .resourceInfo(resourceInfo())
                .port(8081)
                .build();
        List<AppDeploymentEntity> appDeployments = List.of(apiServer, worker);

        when(appDeploymentRepository.findAllByProjectId("123")).thenReturn(appDeployments);

        appDeploymentService.deleteAppDeploymentListByProjectId(123L);

        verify(resourceClient).deleteAppDeployment("123", "api-server");
        verify(resourceClient).deleteAppDeployment("123", "worker");
        verify(appDeploymentRepository).deleteAll(appDeployments);
        verifyNoMoreInteractions(projectClient, githubAllowedRepoRepository);
    }

    @Test
    void deleteAppDeploymentListByProjectIdIgnoresResourceNotFoundAndDeletesStaleApps() {
        AppDeploymentEntity apiServer = AppDeploymentEntity.builder()
                .name("api-server")
                .projectId("123")
                .resourceInfo(resourceInfo())
                .port(8080)
                .build();
        AppDeploymentEntity worker = AppDeploymentEntity.builder()
                .name("worker")
                .projectId("123")
                .resourceInfo(resourceInfo())
                .port(8081)
                .build();
        List<AppDeploymentEntity> appDeployments = List.of(apiServer, worker);

        when(appDeploymentRepository.findAllByProjectId("123")).thenReturn(appDeployments);
        when(resourceClient.deleteAppDeployment("123", "api-server"))
                .thenThrow(new FeignClientBadRequestException(404, "{\"message\":\"app not found\"}"));

        appDeploymentService.deleteAppDeploymentListByProjectId(123L);

        verify(resourceClient).deleteAppDeployment("123", "api-server");
        verify(resourceClient).deleteAppDeployment("123", "worker");
        verify(appDeploymentRepository).deleteAll(appDeployments);
        verifyNoMoreInteractions(projectClient, githubAllowedRepoRepository);
    }

    @Test
    void deleteAppDeploymentListByProjectIdRethrowsUnexpectedClientErrors() {
        AppDeploymentEntity apiServer = AppDeploymentEntity.builder()
                .name("api-server")
                .projectId("123")
                .resourceInfo(resourceInfo())
                .port(8080)
                .build();
        AppDeploymentEntity worker = AppDeploymentEntity.builder()
                .name("worker")
                .projectId("123")
                .resourceInfo(resourceInfo())
                .port(8081)
                .build();
        List<AppDeploymentEntity> appDeployments = List.of(apiServer, worker);

        when(appDeploymentRepository.findAllByProjectId("123")).thenReturn(appDeployments);
        when(resourceClient.deleteAppDeployment("123", "api-server"))
                .thenThrow(new FeignClientBadRequestException(409, "{\"message\":\"conflict\"}"));

        assertThrows(FeignClientBadRequestException.class,
                () -> appDeploymentService.deleteAppDeploymentListByProjectId(123L));

        verify(resourceClient).deleteAppDeployment("123", "api-server");
        verify(resourceClient, never()).deleteAppDeployment("123", "worker");
        verify(appDeploymentRepository, never()).deleteAll(any());
        verifyNoMoreInteractions(projectClient, githubAllowedRepoRepository);
    }

    private ResourceInfo resourceInfo() {
        return ResourceInfo.builder()
                .cpu(0.5)
                .memory(1.0)
                .disk(2)
                .build();
    }
}
