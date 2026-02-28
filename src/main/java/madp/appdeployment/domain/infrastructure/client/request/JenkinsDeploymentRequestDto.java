package madp.appdeployment.domain.infrastructure.client.request;

import lombok.Builder;
import lombok.NonNull;

@Builder
public record JenkinsDeploymentRequestDto(
    @NonNull
    String repositoryFullName,
    @NonNull
    String branch,
    @NonNull
    String imageName,
    @NonNull
    Long repositoryId
) {}