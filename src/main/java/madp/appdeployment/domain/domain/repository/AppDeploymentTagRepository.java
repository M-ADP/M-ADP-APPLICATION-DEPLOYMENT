package madp.appdeployment.domain.domain.repository;

import madp.appdeployment.domain.domain.entity.AppDeploymentEntity;
import madp.appdeployment.domain.domain.entity.AppDeploymentTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface AppDeploymentTagRepository extends JpaRepository<AppDeploymentTagEntity, Long> {
    void deleteAllByAppDeployment(AppDeploymentEntity appDeployment);

    void deleteAllByAppDeploymentIn(Collection<AppDeploymentEntity> appDeployments);
}
