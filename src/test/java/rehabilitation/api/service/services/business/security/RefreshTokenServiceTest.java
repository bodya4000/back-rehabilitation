package rehabilitation.api.service.services.business.security;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import rehabilitation.api.service.business.businessServices.securityBusiness.jwtToken.JwtTokenUtil;
import rehabilitation.api.service.business.businessServices.securityBusiness.refreshToken.RefreshTokenService;
import rehabilitation.api.service.entity.sql.UserModel;
import rehabilitation.api.service.entity.sql.security.RefreshToken;
import rehabilitation.api.service.entity.sql.security.Role;
import rehabilitation.api.service.entity.sql.security.UserRole;
import rehabilitation.api.service.exceptionHandling.exception.auth.TokenExpiredException;
import rehabilitation.api.service.exceptionHandling.exception.buisness.NotFoundLoginException;
import rehabilitation.api.service.repositories.jpa.RefreshTokenRepository;
import rehabilitation.api.service.repositories.jpa.UserRepository;
import rehabilitation.api.service.utills.EntityType;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTest {
    @Mock
    private JwtTokenUtil jwtTokenUtil;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @InjectMocks
    private RefreshTokenService underTheTest;

    @Test
    void Should_FindRefreshToken_When_CorrectToken() {
        var refreshToken = generateToken();
        when(refreshTokenRepository
                .findRefreshTokenByToken(refreshToken.getToken()))
                .thenReturn(Optional.of(refreshToken));
        var foundToken = underTheTest.getByToken(refreshToken.getToken());
        assertThat(foundToken).isEqualTo(refreshToken);
        verify(refreshTokenRepository, times(1)).findRefreshTokenByToken(refreshToken.getToken());
    }


    @Test
    void Should_CreateNewRefreshToken() throws NotFoundLoginException {
        // given
        UserModel user = generateUser(1);
        UserDetails userDetails = new User("login", "password", List.of());

        // when
        when(jwtTokenUtil.refreshLifeTime()).thenReturn(1000L);
        when(userRepository.findByLogin(userDetails.getUsername())).thenReturn(Optional.of(user));

        underTheTest.createRefreshToken(userDetails);
        // then
        verify(userRepository, times(1)).findByLogin(userDetails.getUsername());
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }

    @Test
    void Should_ThrowTokenExpiredException_When_RefreshTokenIsExpired() {
        // given
        var token = generateExpiredToken();

        // when
        assertThatThrownBy(() -> underTheTest.verifyExpiration(token))
                .isInstanceOf(TokenExpiredException.class);

        verify(refreshTokenRepository, times(1)).delete(token);
    }

    @Test
    void Should_RemoveRefreshTokenInDB() {
        //given
        var refreshToken = generateToken();
        // when
        underTheTest.removeRefreshToken(refreshToken);

        verify(refreshTokenRepository, times(1)).delete(refreshToken);
    }

    private RefreshToken generateToken() {
        RefreshToken token = new RefreshToken();
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(Instant.now().plusMillis(1000 * 60 * 60));
        token.setUserModel(new UserModel());
        return token;
    }

    private RefreshToken generateExpiredToken() {
        RefreshToken token = new RefreshToken();
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(Instant.now().minusMillis(1000 * 60 * 60));
        token.setUserModel(new UserModel());
        return token;
    }

    private UserModel generateUser(int index) {
        UserModel user = new UserModel();
        user.setLogin(EntityType.CLIENT.name() + index);
        user.setPassword(EntityType.CLIENT.name());
        user.setEmail(user.getLogin() + "@mail.com");
        user.getRoles().add(new UserRole(Role.ROLE_CLIENT, user));
        return user;
    }
}
