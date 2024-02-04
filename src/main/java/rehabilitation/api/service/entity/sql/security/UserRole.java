package rehabilitation.api.service.entity.sql.security;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import rehabilitation.api.service.entity.sql.UserModel;
import rehabilitation.api.service.entity.sql.security.Role;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "user_roles")
public class UserRole implements GrantedAuthority {

    public UserRole(Role role, UserModel userModel) {
        this.role = role;
        this.userModel = userModel;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @JsonIgnoreProperties("roles")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_login")
    private UserModel userModel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // Constructors, getters, setters, and other methods...

    @Override
    public String getAuthority() {
        return role.getRoleName();
    }
}