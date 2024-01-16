package rehabilitation.api.service.business;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
import rehabilitation.api.service.repositories.SpecialistRepository;
import rehabilitation.api.service.util.JwtTokenUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("clientService")
public class ClientService extends CommonService<ClientModel, ClientDto> {

    private final ClientRepository clientRepository;
    private final SpecialistRepository specialistRepository;

    public ClientService(ClientRepository clientRepository, SpecialistRepository specialistRepository) {
        this.clientRepository = clientRepository;
        this.specialistRepository = specialistRepository;
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
    @Cacheable(value = "clients", key = "#login", unless = "#result == null")
    public ClientDto getModelViewByLogin(String login) throws NotFoundLoginException {
        var clientModel = getModelIfExists(login, clientRepository);
        List<String> listOfClientsLogin = clientModel.getSpecialists().stream().map(SpecialistModel::getLogin).collect(Collectors.toList());
        return doMapModelDtoAndGet(clientModel, listOfClientsLogin);
    }


    @Override
    @Transactional
    @CacheEvict(value = "clients", key = "#login")
    public void deleteModel(String login) throws NotFoundLoginException {
        var client = getModelIfExists(login, clientRepository);
        clientRepository.delete(client);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "clients", key = "#clientLogin"),
            @CacheEvict(value = "specialists", key = "#specialistLogin")})
    public void addChild(String clientLogin, String specialistLogin) throws NotFoundLoginException {
        var client = getModelIfExists(clientLogin, clientRepository);
        var specialist = getModelIfExists(specialistLogin, specialistRepository);
        client.addSpecialist(specialist);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "clients", key = "#clientLogin"),
            @CacheEvict(value = "specialists", key = "#specialistLogin")})
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
    @CacheEvict(value = "clients", key = "#login")
    public void updateModel(String login, Map<String, Object> updates) throws NotFoundLoginException {
        var currentClient = getModelIfExists(login, clientRepository);
        executeUpdates(updates, currentClient);
    }
}


