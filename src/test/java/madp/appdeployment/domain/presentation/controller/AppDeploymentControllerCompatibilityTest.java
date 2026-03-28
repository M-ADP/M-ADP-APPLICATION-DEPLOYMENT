package madp.appdeployment.domain.presentation.controller;

import madp.appdeployment.domain.application.service.AppDeploymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AppDeploymentControllerCompatibilityTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        AppDeploymentService appDeploymentService = mock(AppDeploymentService.class);
        AppDeploymentController controller = new AppDeploymentController(appDeploymentService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getAppDeploymentListByProjectIdReturnsLegacyWrappedResponse() throws Exception {
        mockMvc.perform(get("/apps/projects/123/apps"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("앱 목록을 조회했습니다."));
    }

    @Test
    void getAppDeploymentSummaryReturnsLegacyWrappedResponse() throws Exception {
        mockMvc.perform(post("/apps/summary")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "project_ids": [123, 456]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("배포 요약을 조회했습니다."));
    }
}
