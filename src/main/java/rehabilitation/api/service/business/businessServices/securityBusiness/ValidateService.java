package rehabilitation.api.service.business.businessServices.securityBusiness;

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
import rehabilitation.api.service.repositories.mongo.ChatUserRepository;

@Service
@RequiredArgsConstructor
public class ValidateService {
    private final UserRepository userRepository;
    private final ChatUserRepository chatUserRepository;
    private final AuthenticationManager authenticationManager;
    public void validateRegistration(RegistrationDto registrationDto) throws AlreadyExistLoginException {
        validateLogin(registrationDto.login());
        validateEmail(registrationDto.email());
    }

    private void validateEmail(String email) throws AlreadyExistLoginException {
        if (userRepository.existsByEmail(email)) {
            throw new AlreadyExistLoginException(email);
        }
    }

    private void validateLogin(String login) throws AlreadyExistLoginException {
        if (userRepository.existsByLogin(login) || chatUserRepository.existsChatUserByLogin(login)) {
            throw new AlreadyExistLoginException(login);
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
