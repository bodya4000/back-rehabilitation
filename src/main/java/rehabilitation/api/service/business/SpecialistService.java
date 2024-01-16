package rehabilitation.api.service.business;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rehabilitation.api.service.dto.RegistrationDto;
import rehabilitation.api.service.entity.SpecialistModel;
import rehabilitation.api.service.exceptionHandling.exception.AlreadyExistLoginException;
import rehabilitation.api.service.exceptionHandling.exception.BadRequestException;
import rehabilitation.api.service.repositories.ClientRepository;
import rehabilitation.api.service.dto.SpecialistDto;
import rehabilitation.api.service.exceptionHandling.exception.NotFoundLoginException;
import rehabilitation.api.service.entity.ClientModel;
import rehabilitation.api.service.repositories.SpecialistRepository;
import rehabilitation.api.service.repositories.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpecialistService extends CommonService<SpecialistModel, SpecialistDto> {

    private final SpecialistRepository specialistRepository;
    private final ClientRepository clientRepository;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "specialists", key = "#login")
    public SpecialistDto getModelViewByLogin(String login) throws NotFoundLoginException {
        var specialistModel = getModelIfExists(login, specialistRepository);
        List<String> listOfClientsLogin = specialistModel.getClients().stream().map(ClientModel::getLogin).collect(Collectors.toList());
        return doMapModelDtoAndGet(specialistModel, listOfClientsLogin);
    }

    /* get specialist dto by login for controller*/
    @Override
    @Transactional(readOnly = true)
    public List<SpecialistDto> getAllModelView() {
        List<SpecialistModel> specialistModels = specialistRepository.findAllBy();
        return specialistModels.stream().map(specialistModel -> {
            List<String> listOfClientsLogin = specialistModel.getClients().stream().map(ClientModel::getLogin).collect(Collectors.toList());
            return doMapModelDtoAndGet(specialistModel, listOfClientsLogin);
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    @CacheEvict(value = "specialists", key = "#login")
    public void updateModel(String login, Map<String, Object> updates) throws NotFoundLoginException {
        var specialist = getModelIfExists(login, specialistRepository);
        executeUpdates(updates, specialist);
    }

    @Override
    @Transactional
    @CacheEvict(value = "specialists", key = "#login")
    public void deleteModel(String login) throws NotFoundLoginException {
        SpecialistModel specialist = getModelIfExists(login, specialistRepository);

        /* first you need delete for specialists_clients and then for specialists */
        for (ClientModel client : specialist.getClients()) {
            client.removeSpecialist(specialist);
        }
        specialistRepository.delete(specialist);
    }

    @Override
    @Transactional
    @Caching( evict = {
            @CacheEvict(value = "specialists", key = "#specialistLogin"),
            @CacheEvict(value = "clients", key = "#clientLogin"),})
    public void addChild(String specialistLogin, String clientLogin) throws NotFoundLoginException {
        SpecialistModel specialistModel = getModelIfExists(specialistLogin, specialistRepository);
        ClientModel clientModel = getModelIfExists(clientLogin, clientRepository);
        specialistModel.addClient(clientModel);
    }

    @Override
    @Transactional
    @Caching( evict = {
            @CacheEvict(value = "specialists", key = "#specialistLogin"),
            @CacheEvict(value = "clients", key = "#clientLogin"),})
    public void removeChild(String specialistLogin, String clientLogin) throws NotFoundLoginException {
        SpecialistModel specialistModel = getModelIfExists(specialistLogin, specialistRepository);
        ClientModel clientModel = getModelIfExists(clientLogin, clientRepository);
        specialistModel.removeClient(clientModel);
    }

    @Override
    void executeUpdates(Map<String, Object> updates, SpecialistModel currentSpecialist) {
        updates.forEach((key, value) -> {
            switch (key) {
                case "firstName":
                    if (value instanceof String) {
                        currentSpecialist.setFirstName((String) value);
                    }
                    break;
                case "lastName":
                    if (value instanceof String) {
                        currentSpecialist.setLastName((String) value);
                    }
                    break;
                case "city":
                    if (value instanceof String) {
                        currentSpecialist.setCity((String) value);
                    }
                    break;
                case "age":
                    if (value instanceof String) {
                        if (isNumeric(value)) {
                            currentSpecialist.setAge((int) value);
                        } else {
                            throw new BadRequestException(key + " should be an integer");
                        }
                    }
                    break;
                case "type":
                    if (value instanceof String) {
                        currentSpecialist.setType((String) value);
                    }
                    break;
                case "contactInformation":
                    if (value instanceof String) {
                        currentSpecialist.setContactInformation((String) value);
                    }
                    break;
                case "email":
                    if (value instanceof String) {
                        currentSpecialist.setEmail((String) value);
                    }
                    break;
                case "description":
                    if (value instanceof String) {
                        currentSpecialist.setDescription((String) value);
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Unknown key " + key);
            }
        });
    }

    @Override
    SpecialistDto doMapModelDtoAndGet(SpecialistModel specialistModel, List<String> listOfClientsLogin) {
        return new SpecialistDto(
                specialistModel.getLogin(), specialistModel.getFirstName(), specialistModel.getLastName(),
                specialistModel.getCity(), specialistModel.getAge(), specialistModel.getExperience(),
                specialistModel.getRate(), specialistModel.getType(),
                specialistModel.getImgUrl(), specialistModel.getDescription(),
                specialistModel.getReHub() != null ? specialistModel.getReHub().getLogin() : "",
                listOfClientsLogin);
    }


    private static boolean isNumeric(Object str){
        try {
            // Attempt to parse the string to an integer
            Integer.parseInt((String) str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
