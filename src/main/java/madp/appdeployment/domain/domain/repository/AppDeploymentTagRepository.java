package madp.appdeployment.domain.domain.repository;

import madp.appdeployment.domain.domain.entity.AppDeploymentTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppDeploymentTagRepository extends JpaRepository<AppDeploymentTagEntity, Long> {
    Optional<AppDeploymentTagEntity> findByAppDeployment_IdAndVersion(Long appDeploymentId, Integer version);
    List<AppDeploymentTagEntity> findAllByAppDeployment_IdOrderByVersionDesc(Long appDeploymentId);
}
