package rehabilitation.api.service.repositories.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import rehabilitation.api.service.entity.sql.AdminModel;

public interface AdminRepository extends JpaRepository<AdminModel, String> {
}
