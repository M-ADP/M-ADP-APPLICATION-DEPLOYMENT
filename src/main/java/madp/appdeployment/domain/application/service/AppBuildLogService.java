package madp.appdeployment.domain.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import madp.appdeployment.domain.domain.repository.AppDeploymentRepository;
import madp.appdeployment.domain.exception.AppDeploymentNotFoundException;
import madp.appdeployment.domain.exception.AppBuildNotFoundException;
import madp.appdeployment.domain.infrastructure.client.JenkinsClient;
import madp.appdeployment.domain.infrastructure.client.response.JenkinsBuildsResponse;
import madp.appdeployment.domain.presentation.dto.response.AppBuildLogDetailResponseDto;
import madp.appdeployment.domain.presentation.dto.response.AppBuildLogListResponseDto;
import madp.appdeployment.domain.presentation.dto.response.AppLatestBuildLogResponseDto;
import madp.appdeployment.global.properties.JenkinsProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppBuildLogService {

    private static final String BUILDS_TREE = "builds[number,result,timestamp,duration,actions[parameters[name,value]]]";

    private final AppDeploymentRepository appDeploymentRepository;
    private final JenkinsClient jenkinsClient;
    private final JenkinsProperties jenkinsProperties;

    @Transactional(readOnly = true)
    public AppBuildLogListResponseDto getBuildLogs(String projectId, String appName) {
        Long appId = appDeploymentRepository.findByProjectIdAndName(projectId, appName)
                .orElseThrow(AppDeploymentNotFoundException::new)
                .getId();

        List<AppBuildLogListResponseDto.AppBuildResponse> builds = fetchFilteredBuilds(appId.toString());

        log.info("[getBuildLogs] projectId={}, appName={}, appId={}, count={}", projectId, appName, appId, builds.size());
        return new AppBuildLogListResponseDto(appId.toString(), builds);
    }

    @Transactional(readOnly = true)
    public AppBuildLogDetailResponseDto getBuildLogDetail(String projectId, String appName, Integer buildNumber) {
        appDeploymentRepository.findByProjectIdAndName(projectId, appName)
                .orElseThrow(AppDeploymentNotFoundException::new);

        String logs = jenkinsClient.getConsoleLog(buildNumber, authorization());
        log.info("[getBuildLogDetail] projectId={}, appName={}, buildNumber={}", projectId, appName, buildNumber);
        return new AppBuildLogDetailResponseDto(buildNumber, logs);
    }

    @Transactional(readOnly = true)
    public AppLatestBuildLogResponseDto getLatestBuildLog(String projectId, String appName) {
        Long appId = appDeploymentRepository.findByProjectIdAndName(projectId, appName)
                .orElseThrow(AppDeploymentNotFoundException::new)
                .getId();

        List<AppBuildLogListResponseDto.AppBuildResponse> builds = fetchFilteredBuilds(appId.toString());

        AppBuildLogListResponseDto.AppBuildResponse latest = builds.stream()
                .max(Comparator.comparingInt(AppBuildLogListResponseDto.AppBuildResponse::number))
                .orElseThrow(AppBuildNotFoundException::new);

        String logs = jenkinsClient.getConsoleLog(latest.number(), authorization());
        log.info("[getLatestBuildLog] projectId={}, appName={}, appId={}, buildNumber={}", projectId, appName, appId, latest.number());

        return new AppLatestBuildLogResponseDto(
                appId.toString(),
                latest.number(),
                latest.result(),
                latest.timestamp(),
                latest.duration(),
                logs
        );
    }

    private List<AppBuildLogListResponseDto.AppBuildResponse> fetchFilteredBuilds(String targetAppId) {
        JenkinsBuildsResponse response = jenkinsClient.getBuilds(BUILDS_TREE, authorization());
        return response.builds().stream()
                .filter(build -> isBuildForApp(build, targetAppId))
                .map(build -> new AppBuildLogListResponseDto.AppBuildResponse(
                        build.number(),
                        build.result(),
                        build.timestamp(),
                        build.duration()
                ))
                .toList();
    }

    private boolean isBuildForApp(JenkinsBuildsResponse.JenkinsBuildResponse build, String targetAppId) {
        if (build.actions() == null) return false;
        return build.actions().stream()
                .filter(action -> action.parameters() != null)
                .anyMatch(action -> action.parameters().stream()
                        .anyMatch(p -> "app_id".equals(p.name()) && targetAppId.equals(String.valueOf(p.value())))
                );
    }

    private String authorization() {
        String credentials = jenkinsProperties.getUsername() + ":" + jenkinsProperties.getApiKey();
        return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());
    }
}
