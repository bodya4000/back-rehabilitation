package rehabilitation.api.service.business.businessServices.reHubBusiness.crud;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rehabilitation.api.service.business.businessServices.abstractions.ModelService;
import rehabilitation.api.service.entity.ReHubModel;
import rehabilitation.api.service.entity.SpecialistModel;
import rehabilitation.api.service.entity.UserModel;
import rehabilitation.api.service.exceptionHandling.exception.NotFoundLoginException;
import rehabilitation.api.service.repositories.jpa.ReHubRepository;
import rehabilitation.api.service.repositories.jpa.SpecialistRepository;

import java.util.Map;

import static rehabilitation.api.service.business.businessUtils.ModelValidationUtils.getModelIfExists;

@Service
@RequiredArgsConstructor
public class ReHubCrudService extends ModelService<ReHubModel> {
    private final SpecialistRepository specialistRepository;
    private final ReHubRepository reHubRepository;

    @Override
    @Transactional
    public void updateModel(String login, Map<String, Object> updates) throws NotFoundLoginException {
        var reHub = getModelIfExists(login, reHubRepository);
        executeUpdates(updates, reHub);
    }

    @Override
    @Transactional
    public void deleteModel(String login) throws NotFoundLoginException {
        ReHubModel reHub = getModelIfExists(login, reHubRepository);

        /* first you need delete for specialists_clients and then for specialists */
        for (SpecialistModel specialistModel : reHub.getSpecialists()) {
            reHub.removeSpecialist(specialistModel);
        }
        reHubRepository.delete(reHub);
    }

    @Override
    @Transactional
    public void addChild(String reHubLogin, String specialistLogin) throws NotFoundLoginException {
        ReHubModel reHubModel = getModelIfExists(reHubLogin, reHubRepository);
        SpecialistModel specialistModel = getModelIfExists(specialistLogin, specialistRepository);
        reHubModel.addSpecialist(specialistModel);
    }

    @Override
    @Transactional
    public void removeChild(String reHubLogin, String specialistLogin) throws NotFoundLoginException {
        ReHubModel reHubModel = getModelIfExists(reHubLogin, reHubRepository);
        SpecialistModel specialistModel = getModelIfExists(specialistLogin, specialistRepository);
        reHubModel.removeSpecialist(specialistModel);
    }

    @Override
    protected void executeUpdates(Map<String, Object> updates, UserModel currentUser) {
        var currentReHub = (ReHubModel) currentUser;
        updates.forEach((key, value) -> {
            switch (key) {
                case "name":
                    if (value instanceof String) {
                        currentReHub.setName((String) value);
                    }
                    break;
                case "imgUrl":
                    if (value instanceof String) {
                        currentReHub.setImgUrl((String) value);
                    }
                    break;
                case "contactInformation":
                    if (value instanceof String) {
                        currentReHub.setContactInformation((String) value);
                    }
                    break;
                case "email":
                    if (value instanceof String) {
                        currentReHub.setEmail((String) value);
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
