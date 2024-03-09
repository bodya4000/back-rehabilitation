package rehabilitation.api.service.business.businessServices.clientBusiness.crud;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rehabilitation.api.service.business.businessServices.abstractions.ModelService;
import rehabilitation.api.service.entity.sql.ClientModel;
import rehabilitation.api.service.entity.sql.SpecialistModel;
import rehabilitation.api.service.entity.sql.UserModel;
import rehabilitation.api.service.exceptionHandling.exception.buisness.IllegalPropertyException;
import rehabilitation.api.service.exceptionHandling.exception.buisness.NotFoundLoginException;
import rehabilitation.api.service.repositories.jpa.ClientRepository;
import rehabilitation.api.service.repositories.jpa.SpecialistRepository;

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

        for (SpecialistModel specialistModel : client.getSpecialists()) {
            client.removeSpecialist(specialistModel);
        }
        clientRepository.delete(client);
    }

    @Transactional
    public void addSpecialist(String clientLogin, String specialistLogin) throws NotFoundLoginException {
        ClientModel clientModel = getModelIfExists(clientLogin, clientRepository);
        SpecialistModel specialistModel = getModelIfExists(specialistLogin, specialistRepository);
        clientModel.addSpecialist(specialistModel);
    }

    @Transactional
    public void removeSpecialist(String clientLogin, String specialistLogin) throws NotFoundLoginException {
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
                case "imgUrl":
                    if (value instanceof String) {
                        currentClient.setImgUrl((String) value);
                    }
                    break;
                case "address":
                    if (value instanceof String) {
                        currentClient.setAddress((String) value);
                    }
                    break;
                default:
                    throw new IllegalPropertyException(key);
            }
        });
    }
}
