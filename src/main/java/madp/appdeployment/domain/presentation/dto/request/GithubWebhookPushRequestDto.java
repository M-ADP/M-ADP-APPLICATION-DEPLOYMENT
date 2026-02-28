package madp.appdeployment.domain.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import madp.appdeployment.domain.exception.RequiredValueNullException;

import java.util.List;

public record GithubWebhookPushRequestDto(
        @JsonProperty("ref")
        String ref,
        
        @JsonProperty("repository")
        Repository repository,
        
        @JsonProperty("commits")
        List<GithubWebhookCommitRequestDto> commits
) {
    
    public record Repository(
            @JsonProperty("id") Long id,
            @JsonProperty("full_name") String fullName
    ) {}

    public boolean isBranch(String branchName) {
        if (ref == null || ref.isEmpty() || branchName == null || branchName.isEmpty()) {
            return false;
        }

        String currentBranch = ref.startsWith("refs/heads/")
            ? ref.substring("refs/heads/".length())
            : ref;

        return branchName.equals(currentBranch);
    }

    public String getBranchName() {
        if (ref == null) {
            throw new RequiredValueNullException();
        }
        return ref.startsWith("refs/heads/")
            ? ref.substring("refs/heads/".length())
            : ref;
    }
}