package rehabilitation.api.service.entity.sql;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import rehabilitation.api.service.entity.sql.security.UserRole;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(of = {"login", "email"})
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public class UserModel {

    @Id
    protected String login;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String contactInformation;

    @Column
    private String address;

    @Column
    private String imgUrl;

    @JsonIgnoreProperties("userModel")
    @OneToMany(mappedBy = "userModel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UserRole> roles = new HashSet<>();

    public void addRole(UserRole role) {
        roles.add(role);
        role.setUserModel(this);
    }

    public void removeRole(UserRole role) {
        roles.remove(role);
        role.setUserModel(null);
        role=null;
    }


}