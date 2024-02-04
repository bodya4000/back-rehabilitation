package rehabilitation.api.service.entity.sql;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import rehabilitation.api.service.entity.sql.security.Role;
import rehabilitation.api.service.entity.sql.security.UserRole;

@Getter
@Setter
@Entity
@Table(name = "admins")
@Inheritance(strategy = InheritanceType.JOINED)
public class AdminModel extends UserModel{
    public AdminModel() {
        getRoles().add(new UserRole(Role.ROLE_ADMIN, this));
    }
}
