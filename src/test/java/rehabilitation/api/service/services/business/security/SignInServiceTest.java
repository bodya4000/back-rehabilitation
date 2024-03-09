package rehabilitation.api.service.services.business.security;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import rehabilitation.api.service.business.businessServices.securityBusiness.ValidateService;
import rehabilitation.api.service.business.businessServices.securityBusiness.auth.SignInService;
import rehabilitation.api.service.business.businessServices.securityBusiness.jwtToken.JwtTokenBuilder;
import rehabilitation.api.service.business.businessServices.securityBusiness.refreshToken.RefreshTokenService;
import rehabilitation.api.service.business.businessServices.userBusiness.UserService;
import rehabilitation.api.service.dto.auth.AuthenticateDto;
import rehabilitation.api.service.dto.auth.JwtResponseDto;
import rehabilitation.api.service.entity.sql.security.RefreshToken;
import rehabilitation.api.service.exceptionHandling.exception.auth.WrongPasswordOrLoginException;
import rehabilitation.api.service.exceptionHandling.exception.buisness.NotFoundLoginException;

@ExtendWith(MockitoExtension.class)
public class SignInServiceTest {

    @Mock
    private ValidateService validateService;

    @Mock
    private UserService userService;

    @Mock
    private JwtTokenBuilder jwtTokenBuilder;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private SignInService signInService;

    @Test
    public void Should_SignSuccessfully_When_CredentialsRight() throws WrongPasswordOrLoginException, NotFoundLoginException {
        // Mocking
        AuthenticateDto authenticateDto = new AuthenticateDto("testUser", "testPassword");
        UserDetails userDetails = Mockito.mock(UserDetails.class);
        Mockito.when(userService.loadUserByUsername(authenticateDto.login())).thenReturn(userDetails);
        Mockito.when(jwtTokenBuilder.generateToken(userDetails)).thenReturn("testJwtToken");

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("testRefreshToken");
        Mockito.when(refreshTokenService.createRefreshToken(userDetails)).thenReturn(refreshToken);

        // Testing
        JwtResponseDto jwtResponseDto = signInService.signIn(authenticateDto);

        // Assertion
        Assertions.assertEquals("testJwtToken", jwtResponseDto.accessToken());
        Assertions.assertEquals("testRefreshToken", jwtResponseDto.refreshToken());
    }

    @Test
    public void Should_SignInFailure_When_CredentialsWrong() {
        // Mocking
        AuthenticateDto authenticateDto = new AuthenticateDto("nonexistentUser", "testPassword");
        Mockito.when(userService.loadUserByUsername(authenticateDto.login()))
                .thenThrow(new UsernameNotFoundException("User not found"));

        // Testing and Assertion
        // In this case, we expect the NotFoundLoginException to be thrown
        // when attempting to sign in with a nonexistent user.
        Assert.assertThrows(UsernameNotFoundException.class, () -> signInService.signIn(authenticateDto));
    }
}