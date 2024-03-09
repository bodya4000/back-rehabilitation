package rehabilitation.api.service.business.businessServices.specialistBusiness.crud;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rehabilitation.api.service.business.businessServices.abstractions.ModelService;
import rehabilitation.api.service.entity.sql.ClientModel;
import rehabilitation.api.service.entity.sql.SpecialistModel;
import rehabilitation.api.service.entity.sql.UserModel;
import rehabilitation.api.service.exceptionHandling.exception.auth.BadRequestException;
import rehabilitation.api.service.exceptionHandling.exception.buisness.IllegalPropertyException;
import rehabilitation.api.service.exceptionHandling.exception.buisness.NotFoundLoginException;
import rehabilitation.api.service.repositories.jpa.ClientRepository;
import rehabilitation.api.service.repositories.jpa.SpecialistRepository;
import static rehabilitation.api.service.business.businessUtils.ModelValidationUtils.*;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class SpecialistCrudService extends ModelService<SpecialistModel> {
    private final SpecialistRepository specialistRepository;
    private final ClientRepository clientRepository;

    @Override
    @Transactional
    public void updateModel(String login, Map<String, Object> updates) throws NotFoundLoginException {
        var specialist = getModelIfExists(login, specialistRepository);
        executeUpdates(updates, specialist);
    }

    @Override
    @Transactional
    public void deleteModel(String login) throws NotFoundLoginException {
        SpecialistModel specialist = getModelIfExists(login, specialistRepository);

        /* first you need delete for specialists_clients and then for specialists */
        for (ClientModel client : specialist.getClients()) {
            client.removeSpecialist(specialist);
        }
        if (specialist.getReHub() != null) {
            specialist.getReHub().removeSpecialist(specialist);
        }
        specialistRepository.delete(specialist);
    }

    @Transactional
    public void addClient(String specialistLogin, String clientLogin) throws NotFoundLoginException {
        SpecialistModel specialistModel = getModelIfExists(specialistLogin, specialistRepository);
        ClientModel clientModel = getModelIfExists(clientLogin, clientRepository);
        specialistModel.addClient(clientModel);
    }

    @Transactional
    public void removeClient(String specialistLogin, String clientLogin) throws NotFoundLoginException {
        SpecialistModel specialistModel = getModelIfExists(specialistLogin, specialistRepository);
        ClientModel clientModel = getModelIfExists(clientLogin, clientRepository);
        specialistModel.removeClient(clientModel);
    }

    @Override
    protected void executeUpdates(Map<String, Object> updates, UserModel currentUser) {
        var currentSpecialist = (SpecialistModel) currentUser;
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
                case "contactInformation":
                    if (value instanceof String) {
                        currentSpecialist.setContactInformation((String) value);
                    }
                    break;
                case "imgUrl":
                    if (value instanceof String) {
                        currentSpecialist.setImgUrl((String) value);
                    }
                    break;
                case "address":
                    if (value instanceof String) {
                        currentSpecialist.setAddress((String) value);
                    }
                    break;
                case "city":
                    if (value instanceof String) {
                        currentSpecialist.setCity((String) value);
                    }
                    break;
                case "age":
                    if (value instanceof Integer) currentSpecialist.setAge((int) value);
                    if (value instanceof String) {
                        if (isNumeric(value)) {
                            currentSpecialist.setAge((int) value);
                        } else {
                            throw new BadRequestException(key + " should be an integer");
                        }
                    }
                    break;
                case "speciality":
                    if (value instanceof String) {
                        currentSpecialist.setSpeciality((String) value);
                    }
                    break;
                case "description":
                    if (value instanceof String) {
                        currentSpecialist.setDescription((String) value);
                    }
                    break;
                default:
                    throw new IllegalPropertyException(key);
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
