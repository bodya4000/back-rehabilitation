package rehabilitation.api.service.business.businessServices.clientBusiness.crud;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rehabilitation.api.service.business.businessServices.abstractions.ModelService;
import rehabilitation.api.service.dto.SpecialistDto;
import rehabilitation.api.service.entity.ClientModel;
import rehabilitation.api.service.entity.SpecialistModel;
import rehabilitation.api.service.entity.UserModel;
import rehabilitation.api.service.exceptionHandling.exception.BadRequestException;
import rehabilitation.api.service.exceptionHandling.exception.NotFoundLoginException;
import rehabilitation.api.service.repositories.ClientRepository;
import rehabilitation.api.service.repositories.SpecialistRepository;

import java.util.Map;

import static rehabilitation.api.service.business.businessUtils.ModelValidationUtils.getModelIfExists;

@Service
@RequiredArgsConstructor
public class ClientCrudService extends ModelService<ClientModel> {
    private final SpecialistRepository specialistRepository;
    private final ClientRepository clientRepository;

    @Override
    @Transactional
    public void updateModel(String login, Map<String, Object> updates) throws NotFoundLoginException {
        var client = getModelIfExists(login, clientRepository);
        executeUpdates(updates, client);
    }

    @Override
    @Transactional
    public void deleteModel(String login) throws NotFoundLoginException {
        ClientModel client = getModelIfExists(login, clientRepository);

        /* first you need delete for specialists_clients and then for specialists */
        for (SpecialistModel specialistModel : client.getSpecialists()) {
            client.removeSpecialist(specialistModel);
        }
        clientRepository.delete(client);
    }

    @Override
    @Transactional
    public void addChild(String clientLogin, String specialistLogin) throws NotFoundLoginException {
        ClientModel clientModel = getModelIfExists(clientLogin, clientRepository);
        SpecialistModel specialistModel = getModelIfExists(specialistLogin, specialistRepository);
        clientModel.addSpecialist(specialistModel);
    }

    @Override
    @Transactional
    public void removeChild(String clientLogin, String specialistLogin) throws NotFoundLoginException {
        SpecialistModel specialistModel = getModelIfExists(specialistLogin, specialistRepository);
        ClientModel clientModel = getModelIfExists(clientLogin, clientRepository);
        clientModel.removeSpecialist(specialistModel);
    }

    @Override
    protected void executeUpdates(Map<String, Object> updates, UserModel currentUser) {
        var currentClient = (ClientModel) currentUser;
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
                case "contactInformation":
                    if (value instanceof String) {
                        currentClient.setContactInformation((String) value);
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
