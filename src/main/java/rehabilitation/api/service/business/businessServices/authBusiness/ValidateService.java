package rehabilitation.api.service.business.businessServices.authBusiness;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import rehabilitation.api.service.dto.auth.AuthenticateDto;
import rehabilitation.api.service.dto.auth.RegistrationDto;
import rehabilitation.api.service.exceptionHandling.exception.auth.WrongPasswordOrLoginException;
import rehabilitation.api.service.exceptionHandling.exception.buisness.AlreadyExistLoginException;
import rehabilitation.api.service.repositories.jpa.UserRepository;

@Service
@RequiredArgsConstructor
public class ValidateService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    public void checkIfDataAreUnique(RegistrationDto registrationDto) throws AlreadyExistLoginException {
        if (userRepository.existsByLogin(registrationDto.login())) {
            throw new AlreadyExistLoginException(registrationDto.login());
        }
        if (userRepository.existsByEmail(registrationDto.email())) {
            throw new AlreadyExistLoginException(registrationDto.email());
        }
    }

    public void authenticate(AuthenticateDto authenticateDto) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticateDto.login(), authenticateDto.password()));
        } catch (AuthenticationException e) {
            throw new WrongPasswordOrLoginException("Wrong user or password");
        }
    }
}
