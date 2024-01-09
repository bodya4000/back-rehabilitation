package rehabilitation.api.service.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private ReHubRepository reHubRepository;
    private ClientRepository clientRepository;
    private SpecialistRepository specialistRepository;
    private UserRepository userRepository;
    private AuthenticationManager authenticationManager;
    private UserService userService;
    private JwtTokenUtils jwtTokenUtils;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setReHubRepository(ReHubRepository reHubRepository) {
        this.reHubRepository = reHubRepository;
    }
    @Autowired
    public void setClientRepository(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }
    @Autowired
    public void setSpecialistRepository(SpecialistRepository specialistRepository) {
        this.specialistRepository = specialistRepository;
    }
    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }
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
        } catch (Exception e) {
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
                var client = new ClientModel();
                client.setLogin(registrationDto.login());
                client.setEmail(registrationDto.email());
                client.setPassword(passwordEncoder.encode(registrationDto.password()));
                var role = new UserRole(Role.ROLE_CLIENT, client);
                client.getRoles().add(role);
                clientRepository.save(client);
                return client;
            }
            case SPECIALIST -> {
                var specialist = new SpecialistModel();
                specialist.setLogin(registrationDto.login());
                specialist.setEmail(registrationDto.email());
                specialist.setPassword(passwordEncoder.encode(registrationDto.password()));
                var role = new UserRole(Role.ROLE_SPECIALIST, specialist);
                specialist.getRoles().add(role);
                specialistRepository.save(specialist);
                return specialist;
            }
            case REHUB -> {
                var rehub = new ReHubModel();
                rehub.setLogin(registrationDto.login());
                rehub.setEmail(registrationDto.email());
                rehub.setPassword(passwordEncoder.encode(registrationDto.password()));
                var role = new UserRole(Role.ROLE_REHUB, rehub);
                rehub.getRoles().add(role);
                reHubRepository.save(rehub);
                return rehub;
            }
            default -> throw new BadRequestException("Type of user " + registrationDto.userType() + " is incorrect");
        }


    }

    public void validateUser(RegistrationDto registrationDto) throws AlreadyExistLoginException {
        if (userRepository.existsByLogin(registrationDto.login())) {
            throw new AlreadyExistLoginException(registrationDto.login());
        }
        if (userRepository.existsByEmail(registrationDto.email())) {
            throw new AlreadyExistLoginException(registrationDto.email());
        }
    }

    public void validateRequest(RegistrationDto registrationDto) throws PasswordRegistryException {
        if (!registrationDto.password().equals(registrationDto.confirmedPassword())) {
            throw new PasswordRegistryException();
        }

    }
}
