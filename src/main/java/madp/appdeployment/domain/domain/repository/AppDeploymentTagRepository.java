package madp.appdeployment.domain.domain.repository;

import madp.appdeployment.domain.domain.entity.AppDeploymentTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppDeploymentTagRepository extends JpaRepository<AppDeploymentTagEntity, Long> {
}
