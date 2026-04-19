package madp.appdeployment.domain.infrastructure.client.response;

import java.util.List;

public record JenkinsBuildsResponse(
        List<JenkinsBuildResponse> builds
) {
    public record JenkinsBuildResponse(
            Integer number,
            String result,
            Long timestamp,
            Long duration,
            List<JenkinsActionResponse> actions
    ) {}

    public record JenkinsActionResponse(
            List<JenkinsParameterResponse> parameters
    ) {}

    public record JenkinsParameterResponse(
            String name,
            Object value
    ) {}
}
