package madp.appdeployment.domain.application.service;

import madp.appdeployment.domain.domain.entity.AppDeploymentEntity;
import madp.appdeployment.domain.domain.repository.AppDeploymentRepository;
import madp.appdeployment.domain.domain.repository.AppDeploymentTagRepository;
import madp.appdeployment.domain.domain.repository.GithubAllowedRepoRepository;
import madp.appdeployment.domain.domain.vo.ResourceInfo;
import madp.appdeployment.domain.infrastructure.client.ProjectClient;
import madp.appdeployment.domain.infrastructure.client.ResourceClient;
import madp.appdeployment.domain.infrastructure.client.response.ProjectOwnerResponseDto;
import madp.appdeployment.global.infrastructure.feign.exception.FeignClientBadRequestException;
import madp.appdeployment.global.presentation.dto.response.ApiResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
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
    private AppDeploymentTagRepository appDeploymentTagRepository;

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
        verify(appDeploymentTagRepository).deleteAllByAppDeploymentIn(appDeployments);
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
        verify(appDeploymentTagRepository).deleteAllByAppDeploymentIn(appDeployments);
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
        verify(appDeploymentTagRepository, never()).deleteAllByAppDeploymentIn(any());
        verify(appDeploymentRepository, never()).deleteAll(any());
        verifyNoMoreInteractions(projectClient, githubAllowedRepoRepository);
    }

    @Test
    void deleteAppDeploymentDeletesTagsBeforeDeletingParentRow() {
        AppDeploymentEntity appDeployment = AppDeploymentEntity.builder()
                .name("api-server")
                .projectId("123")
                .resourceInfo(resourceInfo())
                .port(8080)
                .build();

        when(appDeploymentRepository.findById(1L)).thenReturn(Optional.of(appDeployment));
        when(projectClient.getProjectOwner("123")).thenReturn(ownerResponse(true));

        appDeploymentService.deleteAppDeployment(1L);

        InOrder inOrder = inOrder(appDeploymentTagRepository, appDeploymentRepository);
        verify(resourceClient).deleteAppDeployment("123", "api-server");
        inOrder.verify(appDeploymentTagRepository).deleteAllByAppDeployment(appDeployment);
        inOrder.verify(appDeploymentRepository).delete(appDeployment);
        verifyNoMoreInteractions(githubAllowedRepoRepository);
    }

    @Test
    void deleteAppDeploymentListByProjectIdDeletesTagsBeforeParentRows() {
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

        InOrder inOrder = inOrder(appDeploymentTagRepository, appDeploymentRepository);
        inOrder.verify(appDeploymentTagRepository).deleteAllByAppDeploymentIn(appDeployments);
        inOrder.verify(appDeploymentRepository).deleteAll(appDeployments);
        verifyNoMoreInteractions(projectClient, githubAllowedRepoRepository);
    }

    private ResourceInfo resourceInfo() {
        return ResourceInfo.builder()
                .cpu(0.5)
                .memory(1.0)
                .disk(2)
                .build();
    }

    private ApiResponseDto<ProjectOwnerResponseDto> ownerResponse(boolean isOwner) {
        return ApiResponseDto.of("ok", new ProjectOwnerResponseDto(isOwner));
    }
}
