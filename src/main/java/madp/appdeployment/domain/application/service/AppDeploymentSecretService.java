package madp.appdeployment.domain.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import madp.appdeployment.domain.domain.entity.AppDeploymentEntity;
import madp.appdeployment.domain.domain.entity.AppDeploymentSecretEntity;
import madp.appdeployment.domain.domain.repository.AppDeploymentRepository;
import madp.appdeployment.domain.domain.repository.AppDeploymentSecretRepository;
import madp.appdeployment.domain.exception.AppDeploymentNotFoundException;
import madp.appdeployment.domain.exception.SecretNotFoundException;
import madp.appdeployment.domain.infrastructure.client.ResourceClient;
import madp.appdeployment.domain.infrastructure.client.request.CreateSecretRequestDto;
import madp.appdeployment.domain.infrastructure.client.response.SecretCreationResponseDto;
import madp.appdeployment.domain.presentation.dto.response.CreateSecretResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppDeploymentSecretService {

    private final AppDeploymentRepository appDeploymentRepository;
    private final AppDeploymentSecretRepository appDeploymentSecretRepository;
    private final ResourceClient resourceClient;

    @Transactional
    public CreateSecretResponseDto createSecret(String projectId, String appName, Map<String, String> data) {
        log.info("[createSecret] 요청 - projectId={}, appName={}, keyCount={}", projectId, appName, data.size());

        AppDeploymentEntity appDeployment = appDeploymentRepository.findByProjectIdAndName(projectId, appName)
                .orElseThrow(AppDeploymentNotFoundException::new);

        SecretCreationResponseDto resourceResponse =
                resourceClient.createSecret(projectId, appName, new CreateSecretRequestDto(data)).data();

        log.info("[createSecret] Resource 서비스 호출 완료 - path={}", resourceResponse.path());

        List<String> existingNames = appDeploymentSecretRepository.findNamesByAppDeploymentId(appDeployment.getId());
        List<AppDeploymentSecretEntity> secrets = data.entrySet().stream()
                .filter(entry -> !existingNames.contains(entry.getKey()))
                .map(entry -> AppDeploymentSecretEntity.builder()
                        .appDeployment(appDeployment)
                        .name(entry.getKey())
                        .value(entry.getValue())
                        .build())
                .toList();
        appDeploymentSecretRepository.saveAll(secrets);

        log.info("[createSecret] 완료 - appDeploymentId={}, savedCount={}", appDeployment.getId(), secrets.size());

        return new CreateSecretResponseDto(
                resourceResponse.namespace(),
                resourceResponse.appName(),
                resourceResponse.path(),
                resourceResponse.policyName(),
                resourceResponse.roleName()
        );
    }

    @Transactional
    public void deleteSecret(String projectId, String appName, List<String> secretNames) {
        log.info("[deleteSecret] 요청 - projectId={}, appName={}, names={}", projectId, appName, secretNames);

        AppDeploymentEntity appDeployment = appDeploymentRepository.findByProjectIdAndName(projectId, appName)
                .orElseThrow(AppDeploymentNotFoundException::new);

        List<AppDeploymentSecretEntity> secrets =
                appDeploymentSecretRepository.findAllByAppDeploymentIdAndNameIn(appDeployment.getId(), secretNames);

        if (secrets.size() != secretNames.size()) {
            log.warn("[deleteSecret] 존재하지 않는 Secret 이름 포함 - appDeploymentId={}", appDeployment.getId());
            throw new SecretNotFoundException();
        }

        appDeploymentSecretRepository.deleteAll(secrets);
        log.info("[deleteSecret] DB 삭제 완료 - appDeploymentId={}, deletedCount={}", appDeployment.getId(), secrets.size());

        List<AppDeploymentSecretEntity> remaining = appDeploymentSecretRepository.findAllByAppDeploymentId(appDeployment.getId());
        Map<String, String> remainingData = remaining.stream()
                .collect(java.util.stream.Collectors.toMap(AppDeploymentSecretEntity::getName, AppDeploymentSecretEntity::getValue));

        log.info("[deleteSecret] 리소스 서버 createSecret 요청 - remainingCount={}", remainingData.size());
        resourceClient.createSecret(projectId, appName, new CreateSecretRequestDto(remainingData));
        log.info("[deleteSecret] 완료 - projectId={}, appName={}", projectId, appName);
    }

    @Transactional(readOnly = true)
    public List<String> getSecretNames(String projectId, String appName) {
        log.info("[getSecretNames] 요청 - projectId={}, appName={}", projectId, appName);

        AppDeploymentEntity appDeployment = appDeploymentRepository.findByProjectIdAndName(projectId, appName)
                .orElseThrow(AppDeploymentNotFoundException::new);

        List<String> names = appDeploymentSecretRepository.findNamesByAppDeploymentId(appDeployment.getId());
        log.info("[getSecretNames] 완료 - appDeploymentId={}, count={}", appDeployment.getId(), names.size());
        return names;
    }
}
