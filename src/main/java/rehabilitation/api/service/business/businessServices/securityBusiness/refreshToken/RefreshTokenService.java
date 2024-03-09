package rehabilitation.api.service.business.businessServices.securityBusiness.refreshToken;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import rehabilitation.api.service.business.businessServices.securityBusiness.jwtToken.JwtTokenUtil;
import rehabilitation.api.service.entity.sql.security.RefreshToken;
import rehabilitation.api.service.exceptionHandling.exception.auth.InvalidTokenException;
import rehabilitation.api.service.exceptionHandling.exception.auth.TokenExpiredException;
import rehabilitation.api.service.exceptionHandling.exception.buisness.NotFoundLoginException;
import rehabilitation.api.service.repositories.jpa.RefreshTokenRepository;
import rehabilitation.api.service.repositories.jpa.UserRepository;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final JwtTokenUtil jwtTokenUtil;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken getByToken(String token) throws InvalidTokenException {
        return refreshTokenRepository.findRefreshTokenByToken(token)
                .orElseThrow(InvalidTokenException::new);
    }

    public RefreshToken createRefreshToken(UserDetails userDetails) throws NotFoundLoginException {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(jwtTokenUtil.refreshLifeTime()))
                .userModel(userRepository.findByLogin(userDetails.getUsername())
                        .orElseThrow( () -> new NotFoundLoginException(userDetails.getUsername())))
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    public void verifyExpiration(RefreshToken token) throws TokenExpiredException{
        if(token.getExpiryDate().compareTo(Instant.now())<0){
            refreshTokenRepository.delete(token);
            throw new TokenExpiredException();
        }
    }

    public void removeRefreshToken(RefreshToken refreshToken){
        refreshTokenRepository.delete(refreshToken);
    }


}
