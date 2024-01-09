package rehabilitation.api.service.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.persistence.IdClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

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
    private Role role;

    // Constructors, getters, setters, and other methods...

    @Override
    public String getAuthority() {
        return role.getRoleName();
    }
}