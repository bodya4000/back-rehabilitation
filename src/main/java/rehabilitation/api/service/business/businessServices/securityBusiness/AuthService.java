package rehabilitation.api.service.business.businessServices.securityBusiness;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rehabilitation.api.service.business.businessServices.securityBusiness.auth.SignInService;
import rehabilitation.api.service.business.businessServices.securityBusiness.auth.SignUpService;
import rehabilitation.api.service.business.businessServices.securityBusiness.refreshToken.UpdateAccessTokenService;
import rehabilitation.api.service.dto.auth.AuthenticateDto;
import rehabilitation.api.service.dto.auth.JwtResponseDto;
import rehabilitation.api.service.dto.auth.RegistrationDto;
import rehabilitation.api.service.exceptionHandling.exception.auth.InvalidTokenException;
import rehabilitation.api.service.exceptionHandling.exception.buisness.AlreadyExistLoginException;
import rehabilitation.api.service.exceptionHandling.exception.auth.BadRequestException;
import rehabilitation.api.service.exceptionHandling.exception.auth.WrongPasswordOrLoginException;
import rehabilitation.api.service.exceptionHandling.exception.buisness.NotFoundLoginException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final SignInService signInService;
    private final SignUpService signUpService;
    private final UpdateAccessTokenService updateAccessTokenService;

    @Transactional
    public JwtResponseDto signIn(AuthenticateDto authenticateDto) throws WrongPasswordOrLoginException, NotFoundLoginException {
        return signInService.signIn(authenticateDto);
    }

    @Transactional
    public void signUp(RegistrationDto registrationDto) throws AlreadyExistLoginException, BadRequestException {
        signUpService.signUp(registrationDto);
    }

    @Transactional
    public JwtResponseDto updateAccessToken(String token) throws InvalidTokenException, NotFoundLoginException {
        return updateAccessTokenService.updateAccessToken(token);
    }

}