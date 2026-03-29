package madp.appdeployment.domain.presentation.controller;

import madp.appdeployment.domain.application.service.AppDeploymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
    void deleteAppDeploymentListByProjectIdReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/apps/projects/123/apps"))
                .andExpect(status().isNoContent());
    }
}
