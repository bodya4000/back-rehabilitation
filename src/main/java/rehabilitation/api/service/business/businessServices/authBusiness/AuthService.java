package rehabilitation.api.service.business.businessServices.authBusiness;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rehabilitation.api.service.business.businessServices.userBusiness.UserService;
import rehabilitation.api.service.business.businessUtils.RegistrationService;
import rehabilitation.api.service.dto.*;
import rehabilitation.api.service.entity.*;
import rehabilitation.api.service.exceptionHandling.exception.AlreadyExistLoginException;
import rehabilitation.api.service.exceptionHandling.exception.BadRequestException;
import rehabilitation.api.service.exceptionHandling.exception.PasswordRegistryException;
import rehabilitation.api.service.exceptionHandling.exception.WrongPasswordOrLoginException;
import rehabilitation.api.service.repositories.ClientRepository;
import rehabilitation.api.service.repositories.ReHubRepository;
import rehabilitation.api.service.repositories.SpecialistRepository;
import rehabilitation.api.service.repositories.UserRepository;
import rehabilitation.api.service.util.JwtTokenUtils;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RegistrationService registrationService;
    private final UserRepository userRepository;
    private AuthenticationManager authenticationManager;
    private UserService userService;
    private JwtTokenUtils jwtTokenUtils;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {this.authenticationManager = authenticationManager;}
    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
    @Autowired
    public void setJwtTokenUtils(JwtTokenUtils jwtTokenUtils) {
        this.jwtTokenUtils = jwtTokenUtils;
    }
    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public AuthenticationResponse signIn(AuthenticateDto authenticateDto) throws WrongPasswordOrLoginException {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticateDto.login(), authenticateDto.password()));
        } catch (AuthenticationException e) {
            throw new WrongPasswordOrLoginException("Wrong user or password");
        }
        UserDetails userDetails = userService.loadUserByUsername(authenticateDto.login());
        UserDto userDto = userRepository.findDtoByLogin(authenticateDto.login()).get();
        String jwtToken = jwtTokenUtils.generateToken(userDetails);
        return new AuthenticationResponse(
                new JwtResponse(jwtToken),
                userDto
        );
    }


    @Transactional
    public UserModel signUp(RegistrationDto registrationDto) throws AlreadyExistLoginException, PasswordRegistryException, BadRequestException {
        validateRequest(registrationDto);
        validateUser(registrationDto);

        switch (registrationDto.userType()){
            case CLIENT -> {
                return registrationService.registerClient(registrationDto);
            }
            case SPECIALIST -> {
                return registrationService.registerSpecialist(registrationDto);

            }
            case REHUB -> {
                return registrationService.registerReHub(registrationDto);
            }
            case ADMIN -> {
                var admin = new UserModel();
                admin.setLogin(registrationDto.login());
                admin.setEmail(registrationDto.email());
                admin.setPassword(passwordEncoder.encode(registrationDto.password()));
                var role = new UserRole(Role.ROLE_ADMIN, admin);
                admin.getRoles().add(role);
                userRepository.save(admin);
                return admin;
            }
            default -> throw new BadRequestException("Type of user " + registrationDto.userType() + " is incorrect");
        }


    }


    private void validateUser(RegistrationDto registrationDto) throws AlreadyExistLoginException {
        if (userRepository.existsByLogin(registrationDto.login())) {
            throw new AlreadyExistLoginException(registrationDto.login());
        }
        if (userRepository.existsByEmail(registrationDto.email())) {
            throw new AlreadyExistLoginException(registrationDto.email());
        }
    }

    private void validateRequest(RegistrationDto registrationDto) throws PasswordRegistryException {
        if (!registrationDto.password().equals(registrationDto.confirmedPassword())) {
            throw new PasswordRegistryException();
        }

    }
}
