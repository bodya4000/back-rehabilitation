package rehabilitation.api.service.business.businessServices.securityBusiness.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import rehabilitation.api.service.business.businessServices.securityBusiness.ValidateService;
import rehabilitation.api.service.business.businessServices.securityBusiness.jwtToken.JwtTokenBuilder;
import rehabilitation.api.service.business.businessServices.securityBusiness.refreshToken.RefreshTokenService;
import rehabilitation.api.service.business.businessServices.userBusiness.UserService;
import rehabilitation.api.service.dto.auth.AuthenticateDto;
import rehabilitation.api.service.dto.auth.JwtResponseDto;
import rehabilitation.api.service.entity.sql.security.RefreshToken;
import rehabilitation.api.service.exceptionHandling.exception.auth.WrongPasswordOrLoginException;
import rehabilitation.api.service.exceptionHandling.exception.buisness.NotFoundLoginException;

@Service
@RequiredArgsConstructor
public class SignInService {

    private final ValidateService validateService;
    private final UserService userService;
    private final JwtTokenBuilder jwtTokenBuilder;
    private final RefreshTokenService refreshTokenService;

    public JwtResponseDto signIn(AuthenticateDto authenticateDto) throws WrongPasswordOrLoginException, NotFoundLoginException {
        performValidation(authenticateDto);
        UserDetails userDetails = fetchUserDetails(authenticateDto);
        String jwtToken = generateJwtToken(userDetails);
        RefreshToken refreshToken = creteRefreshToken(userDetails);
        return new JwtResponseDto(
                jwtToken,
                refreshToken.getToken()
        );
    }

    private RefreshToken creteRefreshToken(UserDetails userDetails) throws NotFoundLoginException {
        return refreshTokenService.createRefreshToken(userDetails);
    }

    private String generateJwtToken(UserDetails userDetails) {
        return jwtTokenBuilder.generateToken(userDetails);
    }

    private UserDetails fetchUserDetails(AuthenticateDto authenticateDto) {
        return userService.loadUserByUsername(authenticateDto.login());
    }

    private void performValidation(AuthenticateDto authenticateDto) {
        validateService.authenticate(authenticateDto);
    }

}
