package madp.appdeployment.domain.domain.repository;

import madp.appdeployment.domain.domain.entity.AppDeploymentSecretEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppDeploymentSecretRepository extends JpaRepository<AppDeploymentSecretEntity, Long> {

    @Query("select s from AppDeploymentSecretEntity s where s.appDeployment.id = :appDeploymentId")
    List<AppDeploymentSecretEntity> findAllByAppDeploymentId(Long appDeploymentId);

    @Query("select s.name from AppDeploymentSecretEntity s where s.appDeployment.id = :appDeploymentId")
    List<String> findNamesByAppDeploymentId(Long appDeploymentId);

    @Query("select count(s) > 0 from AppDeploymentSecretEntity s where s.appDeployment.id = :appDeploymentId and s.name = :name")
    boolean existsByAppDeploymentIdAndName(Long appDeploymentId, String name);

    @Query("select s from AppDeploymentSecretEntity s where s.appDeployment.id = :appDeploymentId and s.name in :names")
    List<AppDeploymentSecretEntity> findAllByAppDeploymentIdAndNameIn(Long appDeploymentId, List<String> names);
}