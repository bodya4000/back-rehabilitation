package rehabilitation.api.service.entity.sql.security;

import jakarta.persistence.*;
import lombok.*;
import rehabilitation.api.service.entity.sql.UserModel;

import java.time.Instant;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String token;
    private Instant expiryDate;
    @ManyToOne
    @JoinColumn(name = "user_login", referencedColumnName = "login")
    private UserModel userModel;

}
