package rehabilitation.api.service.entity.sql.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

@Getter
public enum Role {
    ROLE_CLIENT("ROLE_CLIENT"),
    ROLE_SPECIALIST("ROLE_SPECIALIST"),
    ROLE_REHUB("ROLE_REHUB"),
    ROLE_ADMIN("ROLE_ADMIN");

    private final String roleName;

    Role(String roleName) {
        this.roleName = roleName;
    }

}