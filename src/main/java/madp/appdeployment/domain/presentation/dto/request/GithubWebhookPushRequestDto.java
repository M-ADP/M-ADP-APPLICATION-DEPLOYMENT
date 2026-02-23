package madp.appdeployment.domain.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

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
    
    public String getOwner() {
        String repositoryFullName = repository().fullName();
        if (repositoryFullName == null || !repositoryFullName.contains("/")) {
            return null;
        }
        return repositoryFullName.split("/")[0];
    }
    
    public String getRepoName() {
        String repositoryFullName = repository().fullName();
        if (repositoryFullName == null || !repositoryFullName.contains("/")) {
            return null;
        }
        return repositoryFullName.split("/")[1];
    }

    public String getBranchName() {
        if (ref == null) {
            return null;
        }
        return ref.startsWith("refs/heads/")
            ? ref.substring("refs/heads/".length())
            : ref;
    }
}