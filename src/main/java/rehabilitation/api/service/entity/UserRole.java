package rehabilitation.api.service.entity;

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

    public UserRole(Role role, ClientModel client) {
        this.role = role;
        this.client = client;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_login")
    private ClientModel client;

    @Enumerated(EnumType.STRING)
    private Role role;

    // Constructors, getters, setters, and other methods...

    @Override
    public String getAuthority() {
        return role.getRoleName();
    }
}