package rehabilitation.api.service.business;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import rehabilitation.api.service.dto.AuthenticateDto;
import rehabilitation.api.service.dto.ClientDto;
import rehabilitation.api.service.dto.JwtResponse;
import rehabilitation.api.service.dto.RegistrationDto;
import rehabilitation.api.service.entity.*;

import rehabilitation.api.service.exceptionHandling.exception.AlreadyExistLoginException;
import rehabilitation.api.service.exceptionHandling.exception.NotFoundLoginException;

import rehabilitation.api.service.exceptionHandling.exception.PasswordRegistryException;
import rehabilitation.api.service.exceptionHandling.exception.WrongPasswordOrLoginException;
import rehabilitation.api.service.repositories.ClientRepository;
import rehabilitation.api.service.repositories.SpecialistRepositoryImpl;
import rehabilitation.api.service.util.JwtTokenUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("clientService")
@RequiredArgsConstructor
public class ClientService extends CommonService<ClientModel, ClientDto> {

    private final ClientRepository clientRepository;

    private final SpecialistRepositoryImpl specialistRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private UserService userService;
    private JwtTokenUtils jwtTokenUtils;

    @Autowired
    public void setJwtTokenUtils(JwtTokenUtils jwtTokenUtils) {
        this.jwtTokenUtils = jwtTokenUtils;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

//    @Autowired
//    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
//        this.authenticationManager = authenticationManager;
//    }

    @Autowired
    public void setPasswordEncoder(BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientDto> getAllModelView() {
        List<ClientModel> clientModels = clientRepository.findAllBy();
        return clientModels.stream().map(clientModel -> {
            List<String> listOfClientsLogin = clientModel.getSpecialists().stream().map(SpecialistModel::getLogin).collect(Collectors.toList());
            return doMapModelDtoAndGet(clientModel, listOfClientsLogin);
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ClientDto getModelViewByLogin(String login) throws NotFoundLoginException {
        var clientModel = getModelIfExists(login, clientRepository);
        List<String> listOfClientsLogin = clientModel.getSpecialists().stream().map(SpecialistModel::getLogin).collect(Collectors.toList());
        return doMapModelDtoAndGet(clientModel, listOfClientsLogin);
    }

    @Override
    @Transactional
    public void signUpModel(RegistrationDto registrationDto) throws AlreadyExistLoginException, PasswordRegistryException {

        if (!registrationDto.password().equals(registrationDto.confirmedPassword())) {
            throw new PasswordRegistryException();
        }

        if (checkIfBaseHasModel(registrationDto.login(), registrationDto.email(), clientRepository)) {
            var client = new ClientModel();
            client.setLogin(registrationDto.login());
            client.setEmail(registrationDto.email());
            client.setPassword(passwordEncoder.encode(registrationDto.password()));
            var role = new UserRole(Role.ROLE_CLIENT, client);
            client.getRoles().add(role);
            clientRepository.save(client);
        }
    }

    @Override
    public void deleteModel(String login) throws NotFoundLoginException {
        var client = getModelIfExists(login, clientRepository);
        clientRepository.delete(client);
    }

    @Override
    @Transactional
    public void addChild(String clientLogin, String specialistLogin) throws NotFoundLoginException {
        var client = getModelIfExists(clientLogin, clientRepository);
        var specialist = getModelIfExists(specialistLogin, specialistRepository);
        client.addSpecialist(specialist);
    }

    @Override
    @Transactional
    public void removeChild(String clientLogin, String specialistLogin) throws NotFoundLoginException {
        ClientModel client = getModelIfExists(clientLogin, clientRepository);
        SpecialistModel specialist = getModelIfExists(specialistLogin, specialistRepository);
        client.removeSpecialist(specialist);
    }

    @Override
    ClientDto doMapModelDtoAndGet(ClientModel clientModel, List<String> listOfSpecialistLogin) {
        return new ClientDto(clientModel.getLogin(), clientModel.getFirstName(), clientModel.getLastName(), clientModel.getEmail(), clientModel.getAddress(), clientModel.getContactInformation(), clientModel.getImgUrl(), listOfSpecialistLogin);
    }

    @Override
    public ClientModel loadModel(String login) throws NotFoundLoginException {
        return getModelIfExists(login, clientRepository);
    }


    @Override
    void executeUpdates(Map<String, Object> updates, ClientModel currentClient) {
        updates.forEach((key, value) -> {
            switch (key) {
                case "firstName":
                    if (value instanceof String) {
                        currentClient.setFirstName((String) value);
                    }
                    break;
                case "lastName":
                    if (value instanceof String) {
                        currentClient.setLastName((String) value);
                    }
                    break;
                case "email":
                    if (value instanceof String) {
                        currentClient.setEmail((String) value);
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Unknown key " + key);
            }
        });
    }

    @Override
    @Transactional
    public void updateModel(String login, Map<String, Object> updates) throws NotFoundLoginException {
        var currentClient = getModelIfExists(login, clientRepository);
        executeUpdates(updates, currentClient);
    }

    public JwtResponse authenticate(AuthenticateDto authenticateDto) throws WrongPasswordOrLoginException, NotFoundLoginException {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticateDto.login(),authenticateDto.password())
            );
        } catch (Exception e) {
            return new JwtResponse("wrong");
        }
        UserDetails userDetails = userService.loadUserByUsername(authenticateDto.login());
        String jwtToken = jwtTokenUtils.generateToken(userDetails);
        return new JwtResponse(jwtToken);
    }
}


