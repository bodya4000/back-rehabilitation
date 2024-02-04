package rehabilitation.api.service.repositories.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import rehabilitation.api.service.entity.sql.security.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    public Optional<RefreshToken> findRefreshTokenByToken(String token);
}
