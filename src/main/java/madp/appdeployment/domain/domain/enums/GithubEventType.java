package madp.appdeployment.domain.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GithubEventType {
    // App Installation 관련
    INSTALLATION("installation"),
    INSTALLATION_REPOSITORIES("installation_repositories"),
    
    // Repository 관련
    PUSH("push"),
    PULL_REQUEST("pull_request"),
    PULL_REQUEST_REVIEW("pull_request_review"),
    PULL_REQUEST_REVIEW_COMMENT("pull_request_review_comment"),
    
    // Branch/Tag 관련
    CREATE("create"),
    DELETE("delete"),
    RELEASE("release"),
    
    // Issue 관련
    ISSUES("issues"),
    ISSUE_COMMENT("issue_comment"),
    
    // Repository 관리
    REPOSITORY("repository"),
    FORK("fork"),
    STAR("star"),
    WATCH("watch"),
    
    // Workflow 관련
    WORKFLOW_RUN("workflow_run"),
    WORKFLOW_JOB("workflow_job"),
    
    // 기타
    PING("ping"),
    COMMIT_COMMENT("commit_comment"),
    GOLLUM("gollum"), // Wiki 변경
    PUBLIC("public"), // Repository public 으로 변경
    MEMBER("member"), // 멤버 추가/제거
    MEMBERSHIP("membership"), // 조직 멤버십 변경
    ORGANIZATION("organization"), // 조직 변경
    TEAM("team"), // 팀 변경
    TEAM_ADD("team_add"); // 팀에 repository 추가
    
    private final String eventName;
}
