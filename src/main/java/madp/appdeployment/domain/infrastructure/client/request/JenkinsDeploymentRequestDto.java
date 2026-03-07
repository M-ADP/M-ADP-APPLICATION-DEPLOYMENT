package madp.appdeployment.domain.infrastructure.client.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.NonNull;

@Builder
public record JenkinsDeploymentRequestDto(
    @NonNull
    @JsonProperty("repository_full_name")
    String repositoryFullName,

    @NonNull
    @JsonProperty("branch")
    String branch,

    @NonNull
    @JsonProperty("image_name")
    String imageName,

    @NonNull
    @JsonProperty("repository_id")
    Long repositoryId
) {}