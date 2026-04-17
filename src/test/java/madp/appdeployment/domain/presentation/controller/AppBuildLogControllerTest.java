package madp.appdeployment.domain.presentation.controller;

import madp.appdeployment.domain.application.service.AppBuildLogService;
import madp.appdeployment.domain.presentation.dto.response.AppBuildLogDetailResponseDto;
import madp.appdeployment.domain.presentation.dto.response.AppBuildLogListResponseDto;
import madp.appdeployment.domain.presentation.dto.response.AppLatestBuildLogResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AppBuildLogControllerTest {

    private MockMvc mockMvc;
    private AppBuildLogService appBuildLogService;

    @BeforeEach
    void setUp() {
        appBuildLogService = mock(AppBuildLogService.class);
        AppBuildLogController controller = new AppBuildLogController(appBuildLogService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getBuildLogsReturnsSuccessfulResponse() throws Exception {
        AppBuildLogListResponseDto responseDto = new AppBuildLogListResponseDto(
                "project-app",
                List.of(new AppBuildLogListResponseDto.AppBuildResponse(10, "SUCCESS", 1618640000000L, 45000L))
        );
        when(appBuildLogService.getBuildLogs(anyString(), anyString())).thenReturn(responseDto);

        mockMvc.perform(get("/apps/project/app/build-logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("App deployment build logs retrieved successfully"))
                .andExpect(jsonPath("$.data.app_id").value("project-app"))
                .andExpect(jsonPath("$.data.builds[0].number").value(10));
    }

    @Test
    void getLatestBuildLogReturnsSuccessfulResponse() throws Exception {
        AppLatestBuildLogResponseDto responseDto = new AppLatestBuildLogResponseDto(
                "project-app", 10, "SUCCESS", 1618640000000L, 45000L, "log content"
        );
        when(appBuildLogService.getLatestBuildLog(anyString(), anyString())).thenReturn(responseDto);

        mockMvc.perform(get("/apps/project/app/build-logs/latest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("App deployment latest build log retrieved successfully"))
                .andExpect(jsonPath("$.data.appId").value("project-app"))
                .andExpect(jsonPath("$.data.number").value(10))
                .andExpect(jsonPath("$.data.logs").value("log content"));
    }

    @Test
    void getBuildLogDetailReturnsSuccessfulResponse() throws Exception {
        AppBuildLogDetailResponseDto responseDto = new AppBuildLogDetailResponseDto(10, "log content");
        when(appBuildLogService.getBuildLogDetail(anyString(), anyString(), anyInt())).thenReturn(responseDto);

        mockMvc.perform(get("/apps/project/app/build-logs/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("App deployment build log detail retrieved successfully"))
                .andExpect(jsonPath("$.data.number").value(10))
                .andExpect(jsonPath("$.data.logs").value("log content"));
    }
}
