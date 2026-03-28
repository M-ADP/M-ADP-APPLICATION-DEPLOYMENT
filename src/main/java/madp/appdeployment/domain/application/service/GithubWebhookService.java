package madp.appdeployment.domain.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.Base64;
import madp.appdeployment.domain.domain.entity.GithubAccountUserEntity;
import madp.appdeployment.domain.domain.entity.GithubAllowedRepoEntity;
import madp.appdeployment.domain.domain.entity.GithubInstallationEntity;
import madp.appdeployment.domain.domain.enums.AppDeploymentStatus;
import madp.appdeployment.domain.domain.enums.GitHubAccountType;
import madp.appdeployment.domain.domain.enums.GithubUserType;
import madp.appdeployment.domain.domain.repository.AppDeploymentRepository;
import madp.appdeployment.domain.domain.repository.GithubAllowedRepoRepository;
import madp.appdeployment.domain.domain.repository.GithubInstallationRepository;
import madp.appdeployment.domain.domain.repository.GithubAccountUserRepository;
import madp.appdeployment.domain.exception.InstallationNotFoundException;
import madp.appdeployment.domain.exception.AppDeploymentNotFoundException;
import madp.appdeployment.domain.infrastructure.client.JenkinsClient;
import madp.appdeployment.domain.infrastructure.client.UserClient;
import madp.appdeployment.domain.infrastructure.client.request.JenkinsDeploymentRequestDto;
import madp.appdeployment.domain.infrastructure.client.response.UserProfileResponseDto;
import madp.appdeployment.domain.infrastructure.github.token.GithubAppTokenManager;
import madp.appdeployment.domain.infrastructure.client.GithubClient;
import madp.appdeployment.domain.infrastructure.client.response.GithubMemberResponse;
import madp.appdeployment.domain.presentation.dto.request.GithubRepositoryRequestDto;
import madp.appdeployment.domain.presentation.dto.request.GithubWebhookInstallationRequestDto;
import madp.appdeployment.domain.presentation.dto.request.GithubWebhookOrganizationRequestDto;
import madp.appdeployment.domain.presentation.dto.request.GithubWebhookPushRequestDto;
import madp.appdeployment.domain.domain.entity.AppDeploymentEntity;
import madp.appdeployment.domain.presentation.dto.response.GithubAllowedRepositoryResponseDto;
import madp.appdeployment.global.presentation.dto.response.ApiResponseDto;
import madp.appdeployment.global.properties.JenkinsProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GithubWebhookService {
    private final GithubInstallationRepository githubInstallationRepository;
    private final GithubAllowedRepoRepository githubAllowedRepoRepository;
    private final AppDeploymentRepository appDeploymentRepository;
    private final GithubAccountUserRepository githubAccountUserRepository;
    private final UserClient userClient;
    private final GithubAppTokenManager githubAppTokenManager;
    private final GithubClient githubClient;
    private final JenkinsClient jenkinsClient;
    private final JenkinsProperties jenkinsProperties;

    @Transactional
    public void installGithubApp(GithubWebhookInstallationRequestDto githubWebhookInstallationRequestDto) {
        Long installationId = githubWebhookInstallationRequestDto.installation().id();
        Long accountId = githubWebhookInstallationRequestDto.installation().account().id();

        log.info("[installGithubApp] 요청 - installationId={}, accountId={}", installationId, accountId);

        saveInstallation(githubWebhookInstallationRequestDto, installationId, accountId);
        saveAllowedRepositories(githubWebhookInstallationRequestDto.repositories(), installationId, accountId);
        saveMembers(githubWebhookInstallationRequestDto, installationId);

        log.info("[installGithubApp] 완료 - installationId={}, repositoryCount={}",
                installationId, githubWebhookInstallationRequestDto.repositories().size());
    }

    @Transactional
    public void uninstallGithubApp(Long installationId) {
        log.info("[uninstallGithubApp] 요청 - installationId={}", installationId);

        // 자식 테이블부터 삭제 (Foreign Key 제약조건 때문)
        appDeploymentRepository.deleteAllByInstallationId(installationId);
        githubAccountUserRepository.deleteAllByInstallationId(installationId);
        githubAllowedRepoRepository.deleteAllByInstallationId(installationId);
        githubInstallationRepository.deleteAllByInstallationId(installationId);

        log.info("[uninstallGithubApp] 완료 - installationId={}", installationId);
    }

    @Transactional
    public void updateRepositories(GithubWebhookInstallationRequestDto githubWebhookInstallationRequestDto) {
        List<GithubRepositoryRequestDto> githubAddRepositories = githubWebhookInstallationRequestDto.repositoriesAdded();
        List<GithubRepositoryRequestDto> githubRemoveRepositories = githubWebhookInstallationRequestDto.repositoriesRemoved();
        List<GithubAllowedRepoEntity> githubAllowedAddRepositories = new ArrayList<>();

        log.info("[updateRepositories] 요청 - installationId={}, addCount={}, removeCount={}",
                githubWebhookInstallationRequestDto.installation().id(),
                githubAddRepositories.size(), githubRemoveRepositories.size());

        GithubInstallationEntity installation = githubInstallationRepository.findByInstallationId(githubWebhookInstallationRequestDto.installation().id())
                .orElseThrow(() -> new InstallationNotFoundException("Installation을 찾을 수 없습니다: " + githubWebhookInstallationRequestDto.installation().id()));

        for(GithubRepositoryRequestDto addGithubRepository : githubAddRepositories) {
            GithubAllowedRepoEntity gitHubAllowedRepoEntity = GithubAllowedRepoEntity.builder()
                    .installation(installation)
                    .repositoryId(addGithubRepository.id())
                    .accountId(githubWebhookInstallationRequestDto.installation().account().id())
                    .repositoryFullName(addGithubRepository.fullName())
                    .build();
            githubAllowedAddRepositories.add(gitHubAllowedRepoEntity);
        }
        List<Long> removeRepositoryIds = githubRemoveRepositories.stream()
                .map(GithubRepositoryRequestDto::id)
                .toList();

        if(!githubAllowedAddRepositories.isEmpty()) {
            githubAllowedRepoRepository.saveAll(githubAllowedAddRepositories);
        }
        if (!removeRepositoryIds.isEmpty()) {
            githubAllowedRepoRepository.deleteAllByRepositoryIdIn(removeRepositoryIds);
        }

        log.info("[updateRepositories] 완료 - installationId={}", githubWebhookInstallationRequestDto.installation().id());
    }

    /**
     * 현재 사용자가 접근 가능한 모든 GitHub Repository 목록을 조회
     * <p>
     * 로직:
     * 1. 현재 사용자의 GitHub ID 조회 (UserClient 통해)
     * 2. 사용자가 속한 모든 GitHub 계정(개인/조직) ID 조회 (GithubAccountUserEntity 통해)
     * 3. 해당 계정들의 허용된 Repository 목록 조회 (GitHubAllowedRepoEntity 통해)
     * 4. Installation의 avatarUrl과 함께 응답 DTO로 변환
     * 
     * @return 사용자가 접근 가능한 GitHub Repository 목록
     */
    public List<GithubAllowedRepositoryResponseDto> getAllowedRepositories() {
        log.info("[getAllowedRepositories] 요청");

        ApiResponseDto<UserProfileResponseDto> userProfileResponseDto = userClient.getUserProfile();
        Long userGithubId = userProfileResponseDto.data().id();
        log.info("[getAllowedRepositories] 유저 GitHub ID 조회 완료 - userGithubId={}", userGithubId);

        // 사용자가 속한 모든 GitHub 계정(개인/조직) ID 조회
        List<Long> userAllGithubAccounts = githubAccountUserRepository.findAllGithubAccountIdByGithubUserId(userGithubId);

        if (userAllGithubAccounts.isEmpty()) {
            log.info("[getAllowedRepositories] 소속된 GitHub 계정 없음 - userGithubId={}", userGithubId);
            return new ArrayList<>();
        }

        // 해당 계정들의 허용된 Repository 목록을 Installation과 함께 조회 (fetch join 으로 성능 최적화)
        List<GithubAllowedRepoEntity> allowedRepositories = githubAllowedRepoRepository.findAllByAccountIdInWithInstallation(userAllGithubAccounts);

        List<GithubAllowedRepositoryResponseDto> result = allowedRepositories.stream()
                .map(this::convertToResponseDto)
                .toList();

        log.info("[getAllowedRepositories] 완료 - userGithubId={}, repositoryCount={}", userGithubId, result.size());
        return result;
    }
    
    /**
     * GitHubAllowedRepoEntity를 GithubAllowedRepositoryResponseDto로 변환
     * 연관관계를 통해 Installation의 avatarUrl 을 직접 접근 (이미 fetch join 으로 로딩됨)
     */
    private GithubAllowedRepositoryResponseDto convertToResponseDto(GithubAllowedRepoEntity entity) {
        return GithubAllowedRepositoryResponseDto.builder()
                .repositoryProfile(entity.getInstallation().getAvatarUrl())
                .repositoryFullName(entity.getRepositoryFullName())
                .build();
    }

    private void saveAllowedRepositories(List<GithubRepositoryRequestDto> repositories, Long installationId, Long accountId) {
        // Installation Entity를 조회하여 연관관계 설정
        GithubInstallationEntity installation = githubInstallationRepository.findByInstallationId(installationId)
                .orElseThrow(() -> new InstallationNotFoundException("Installation을 찾을 수 없습니다: " + installationId));
                
        List<GithubAllowedRepoEntity> githubAllowedRepositories = new ArrayList<>();

        for(GithubRepositoryRequestDto githubRepositoryRequestDto : repositories) {
            GithubAllowedRepoEntity gitHubAllowedRepoEntity = GithubAllowedRepoEntity.builder()
                    .installation(installation)
                    .repositoryId(githubRepositoryRequestDto.id())
                    .accountId(accountId)
                    .repositoryFullName(githubRepositoryRequestDto.fullName())
                    .build();
            githubAllowedRepositories.add(gitHubAllowedRepoEntity);
        }
        githubAllowedRepoRepository.saveAll(githubAllowedRepositories);
    }

    private void saveInstallation(GithubWebhookInstallationRequestDto githubWebhookInstallationRequestDto, Long installationId, Long accountId) {
        GithubInstallationEntity githubInstallationEntity = GithubInstallationEntity.builder()
                .installationId(installationId)
                .accountId(accountId)
                .accountType(GitHubAccountType.from(githubWebhookInstallationRequestDto.installation().account().type()))
                .avatarUrl(githubWebhookInstallationRequestDto.installation().account().avatarUrl())
                .build();
        githubInstallationRepository.save(githubInstallationEntity);
    }

    private void saveMembers(GithubWebhookInstallationRequestDto githubWebhookInstallationRequestDto, Long installationId) {
        // Installation Entity를 조회하여 연관관계 설정
        GithubInstallationEntity installation = githubInstallationRepository.findByInstallationId(installationId)
                .orElseThrow(() -> new InstallationNotFoundException("Installation을 찾을 수 없습니다: " + installationId));
        
        if(GitHubAccountType.USER.equals(GitHubAccountType.from(githubWebhookInstallationRequestDto.installation().account().type()))) {
             GithubAccountUserEntity githubAccountUserEntity = GithubAccountUserEntity.builder()
                    .installation(installation)
                    .githubUserId(githubWebhookInstallationRequestDto.installation().account().id())
                    .build();
            githubAccountUserRepository.save(githubAccountUserEntity);
        }
        else {
            String installationAccessToken = githubAppTokenManager.getInstallationAccessToken(installationId.toString());
            List<GithubMemberResponse> memberResponse = githubClient.getOrganizationMembers(githubWebhookInstallationRequestDto.installation().account().login(), installationAccessToken);
            List<GithubAccountUserEntity> githubAccountUsers = memberResponse.stream()
                    .filter(member -> !GithubUserType.BOT.getUserType().equals(member.type()))
                    .map(member -> GithubAccountUserEntity.builder()
                            .installation(installation)
                            .githubUserId(member.id())
                            .build())
                    .toList();
            githubAccountUserRepository.saveAll(githubAccountUsers);
        }
    }

    @Transactional
    public void addMember(GithubWebhookOrganizationRequestDto githubWebhookOrganizationRequestDto) {
        log.info("[addMember] 요청 - organizationId={}, githubId={}, type={}",
                githubWebhookOrganizationRequestDto.organizationId(),
                githubWebhookOrganizationRequestDto.githubId(),
                githubWebhookOrganizationRequestDto.githubType());

        if(GithubUserType.BOT.getUserType().equals(githubWebhookOrganizationRequestDto.githubType())) {
            log.info("[addMember] BOT 계정 무시 - githubId={}", githubWebhookOrganizationRequestDto.githubId());
            return;
        }

        // Organization ID로 Installation 조회
        GithubInstallationEntity installation = githubInstallationRepository.findByAccountId(githubWebhookOrganizationRequestDto.organizationId())
                .orElseThrow(() -> new InstallationNotFoundException("Organization의 Installation을 찾을 수 없습니다: " + githubWebhookOrganizationRequestDto.organizationId()));

        GithubAccountUserEntity githubAccountUserEntity = GithubAccountUserEntity.builder()
                .installation(installation)
                .githubUserId(githubWebhookOrganizationRequestDto.githubId())
                .build();
        githubAccountUserRepository.save(githubAccountUserEntity);
        log.info("[addMember] 완료 - organizationId={}, githubId={}",
                githubWebhookOrganizationRequestDto.organizationId(), githubWebhookOrganizationRequestDto.githubId());
    }

    @Transactional
    public void removeMember(GithubWebhookOrganizationRequestDto githubWebhookOrganizationRequestDto) {
        log.info("[removeMember] 요청 - organizationId={}, githubId={}, type={}",
                githubWebhookOrganizationRequestDto.organizationId(),
                githubWebhookOrganizationRequestDto.githubId(),
                githubWebhookOrganizationRequestDto.githubType());

        if(GithubUserType.BOT.getUserType().equals(githubWebhookOrganizationRequestDto.githubType())) {
            log.info("[removeMember] BOT 계정 무시 - githubId={}", githubWebhookOrganizationRequestDto.githubId());
            return;
        }
        githubAccountUserRepository.deleteByGithubAccountIdAndGithubUserId(githubWebhookOrganizationRequestDto.organizationId(), githubWebhookOrganizationRequestDto.githubId());
        log.info("[removeMember] 완료 - organizationId={}, githubId={}",
                githubWebhookOrganizationRequestDto.organizationId(), githubWebhookOrganizationRequestDto.githubId());
    }

    @Transactional(readOnly = true)
    public void handlePushEvent(GithubWebhookPushRequestDto pushPayload) {
        log.info("[handlePushEvent] 요청 - repositoryId={}, branch={}",
                pushPayload.repository().id(), pushPayload.getBranchName());

        // Repository ID로 AppDeploymentEntity 조회 (여러 deployment가 같은 repository 사용 가능)
        AppDeploymentEntity appDeploymentEntity = appDeploymentRepository.findByGithubRepository_RepositoryId(pushPayload.repository().id()).orElseThrow(AppDeploymentNotFoundException::new);

        if(!pushPayload.isBranch(appDeploymentEntity.getGithubBranch())) {
            log.info("[handlePushEvent] 배포 대상 브랜치 아님 - pushBranch={}, deployBranch={}",
                    pushPayload.getBranchName(), appDeploymentEntity.getGithubBranch());
            return;
        }

        JenkinsDeploymentRequestDto jenkinsDeploymentRequestDto = JenkinsDeploymentRequestDto.builder()
                .repositoryId(pushPayload.repository().id())
                .branch(pushPayload.getBranchName())
                .projectId(appDeploymentEntity.getProjectId())
                .appId(appDeploymentEntity.getId())
                .repositoryFullName(pushPayload.repository().fullName())
                .build();

        String credentials = jenkinsProperties.getUsername() + ":" + jenkinsProperties.getApiKey();
        String authenticationInfo = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());
        String crumb = jenkinsClient.getCrumb(authenticationInfo).crumb();

        log.info("[handlePushEvent] Jenkins 빌드 트리거 - projectId={}, appId={}, repositoryFullName={}, branch={}",
                jenkinsDeploymentRequestDto.projectId(), jenkinsDeploymentRequestDto.appId(),
                jenkinsDeploymentRequestDto.repositoryFullName(), jenkinsDeploymentRequestDto.branch());

        jenkinsClient.triggerJenkins(
                jenkinsDeploymentRequestDto.projectId(),
                jenkinsDeploymentRequestDto.appId(),
                jenkinsDeploymentRequestDto.repositoryFullName(),
                jenkinsDeploymentRequestDto.repositoryId(),
                jenkinsDeploymentRequestDto.branch(),
                authenticationInfo,
                crumb
        );

        appDeploymentEntity.updateStatus(AppDeploymentStatus.BUILDING);
        log.info("[handlePushEvent] 완료 - appDeploymentId={}, status=BUILDING", appDeploymentEntity.getId());
    }
}
