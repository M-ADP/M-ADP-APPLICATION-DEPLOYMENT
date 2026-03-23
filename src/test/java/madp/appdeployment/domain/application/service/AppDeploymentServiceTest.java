package madp.appdeployment.domain.application.service;

import madp.appdeployment.domain.application.support.ProjectResourceLockManager;
import madp.appdeployment.domain.domain.entity.AppDeploymentEntity;
import madp.appdeployment.domain.domain.repository.AppDeploymentRepository;
import madp.appdeployment.domain.domain.repository.GithubAllowedRepoRepository;
import madp.appdeployment.domain.domain.repository.dto.ProjectResourceUsageSumDto;
import madp.appdeployment.domain.domain.vo.ResourceInfo;
import madp.appdeployment.domain.exception.InvalidResourceInfoException;
import madp.appdeployment.domain.infrastructure.client.ProjectClient;
import madp.appdeployment.domain.infrastructure.client.ResourceClient;
import madp.appdeployment.domain.infrastructure.client.request.AppRevisionRequestDto;
import madp.appdeployment.domain.infrastructure.client.response.ProjectOwnerResponseDto;
import madp.appdeployment.domain.infrastructure.client.response.ProjectResourceLimitResponseDto;
import madp.appdeployment.domain.presentation.dto.request.CreateAppDeploymentRequestDto;
import madp.appdeployment.global.presentation.dto.response.ApiResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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

    @Mock
    private ProjectResourceLockManager projectResourceLockManager;

    private AppDeploymentService appDeploymentService;

    @BeforeEach
    void setUp() {
        appDeploymentService = new AppDeploymentService(
                appDeploymentRepository,
                githubAllowedRepoRepository,
                projectClient,
                resourceClient,
                projectResourceLockManager
        );

        lenient().doAnswer(invocation -> {
            Supplier<?> action = invocation.getArgument(1);
            return action.get();
        }).when(projectResourceLockManager).executeWithLock(any(), any(Supplier.class));

        lenient().doAnswer(invocation -> {
            Runnable action = invocation.getArgument(1);
            action.run();
            return null;
        }).when(projectResourceLockManager).executeWithLock(any(), any(Runnable.class));
    }

    @Test
    void createAppDeploymentReturnsIdWhenProjectResourcesAreWithinLimit() {
        CreateAppDeploymentRequestDto requestDto = new CreateAppDeploymentRequestDto(
                "sample-app",
                1.0,
                0.5,
                10,
                "project-1",
                8080
        );
        AppDeploymentEntity savedEntity = AppDeploymentEntity.builder()
                .name("sample-app")
                .projectId("project-1")
                .resourceInfo(ResourceInfo.builder().cpu(1.0).memory(0.5).disk(10).build())
                .port(8080)
                .build();
        setEntityId(savedEntity, 101L);

        when(projectClient.getProjectOwner("project-1"))
                .thenReturn(ApiResponseDto.of("ok", new ProjectOwnerResponseDto(true)));
        when(projectClient.getProjectResourceLimit("project-1"))
                .thenReturn(ApiResponseDto.of("ok", new ProjectResourceLimitResponseDto(1L, 4.0, 2.0, 50.0)));
        when(appDeploymentRepository.sumResourceUsageByProjectId("project-1"))
                .thenReturn(new ProjectResourceUsageSumDto(1.0, 0.5, 10.0));
        when(appDeploymentRepository.save(any(AppDeploymentEntity.class))).thenReturn(savedEntity);

        Long createdId = appDeploymentService.createAppDeployment(requestDto);

        assertEquals(101L, createdId);
        verify(appDeploymentRepository).save(any(AppDeploymentEntity.class));
    }

    @Test
    void createAppDeploymentThrowsWhenCpuLimitIsExceeded() {
        CreateAppDeploymentRequestDto requestDto = new CreateAppDeploymentRequestDto(
                "sample-app",
                1.5,
                0.5,
                10,
                "project-1",
                8080
        );

        when(projectClient.getProjectOwner("project-1"))
                .thenReturn(ApiResponseDto.of("ok", new ProjectOwnerResponseDto(true)));
        when(projectClient.getProjectResourceLimit("project-1"))
                .thenReturn(ApiResponseDto.of("ok", new ProjectResourceLimitResponseDto(1L, 2.0, 2.0, 50.0)));
        when(appDeploymentRepository.sumResourceUsageByProjectId("project-1"))
                .thenReturn(new ProjectResourceUsageSumDto(1.0, 0.5, 10.0));

        assertThrows(
                InvalidResourceInfoException.class,
                () -> appDeploymentService.createAppDeployment(requestDto)
        );

        verify(appDeploymentRepository, never()).save(any(AppDeploymentEntity.class));
    }

    @Test
    void updateAppDeploymentResourceInfoRevisesAppWhenProjectResourcesAreWithinLimit() {
        AppDeploymentEntity appDeploymentEntity = AppDeploymentEntity.builder()
                .name("sample-app")
                .projectId("project-1")
                .resourceInfo(ResourceInfo.builder().cpu(1.0).memory(0.5).disk(10).build())
                .port(8080)
                .build();
        setEntityId(appDeploymentEntity, 77L);
        ResourceInfo updatedResourceInfo = ResourceInfo.builder()
                .cpu(1.5)
                .memory(0.75)
                .disk(12)
                .build();

        when(appDeploymentRepository.findProjectIdById(77L)).thenReturn(Optional.of("project-1"));
        when(appDeploymentRepository.findById(77L)).thenReturn(Optional.of(appDeploymentEntity));
        when(projectClient.getProjectOwner("project-1"))
                .thenReturn(ApiResponseDto.of("ok", new ProjectOwnerResponseDto(true)));
        when(projectClient.getProjectResourceLimit("project-1"))
                .thenReturn(ApiResponseDto.of("ok", new ProjectResourceLimitResponseDto(1L, 4.0, 2.0, 50.0)));
        when(appDeploymentRepository.sumResourceUsageByProjectIdExcludingAppId("project-1", 77L))
                .thenReturn(new ProjectResourceUsageSumDto(1.0, 0.5, 10.0));

        appDeploymentService.updateAppDeploymentResourceInfo(77L, updatedResourceInfo);

        ArgumentCaptor<AppRevisionRequestDto> requestCaptor = ArgumentCaptor.forClass(AppRevisionRequestDto.class);
        verify(resourceClient).reviseApp(requestCaptor.capture());
        assertEquals("1500m", requestCaptor.getValue().maxCpu());
        assertEquals("768Mi", requestCaptor.getValue().maxMemory());
        assertEquals("12288Mi", requestCaptor.getValue().maxDisk());
        assertEquals(1.5, appDeploymentEntity.getResourceInfo().getCpu());
        assertEquals(0.75, appDeploymentEntity.getResourceInfo().getMemory());
        assertEquals(12, appDeploymentEntity.getResourceInfo().getDisk());
    }

    @Test
    void updateAppDeploymentResourceInfoThrowsWhenMemoryLimitIsExceeded() {
        AppDeploymentEntity appDeploymentEntity = AppDeploymentEntity.builder()
                .name("sample-app")
                .projectId("project-1")
                .resourceInfo(ResourceInfo.builder().cpu(1.0).memory(0.5).disk(10).build())
                .port(8080)
                .build();
        setEntityId(appDeploymentEntity, 77L);
        ResourceInfo updatedResourceInfo = ResourceInfo.builder()
                .cpu(1.5)
                .memory(1.75)
                .disk(12)
                .build();

        when(appDeploymentRepository.findProjectIdById(77L)).thenReturn(Optional.of("project-1"));
        when(appDeploymentRepository.findById(77L)).thenReturn(Optional.of(appDeploymentEntity));
        when(projectClient.getProjectOwner("project-1"))
                .thenReturn(ApiResponseDto.of("ok", new ProjectOwnerResponseDto(true)));
        when(projectClient.getProjectResourceLimit("project-1"))
                .thenReturn(ApiResponseDto.of("ok", new ProjectResourceLimitResponseDto(1L, 4.0, 2.0, 50.0)));
        when(appDeploymentRepository.sumResourceUsageByProjectIdExcludingAppId("project-1", 77L))
                .thenReturn(new ProjectResourceUsageSumDto(1.0, 0.5, 10.0));

        assertThrows(
                InvalidResourceInfoException.class,
                () -> appDeploymentService.updateAppDeploymentResourceInfo(77L, updatedResourceInfo)
        );

        verify(resourceClient, never()).reviseApp(any(AppRevisionRequestDto.class));
    }

    private void setEntityId(AppDeploymentEntity appDeploymentEntity, Long id) {
        try {
            Field idField = appDeploymentEntity.getClass().getSuperclass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(appDeploymentEntity, id);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }
}
