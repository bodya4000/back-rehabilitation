package rehabilitation.api.service.business;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rehabilitation.api.service.dto.ClientDto;
import rehabilitation.api.service.dto.RehubDto;
import rehabilitation.api.service.entity.ClientModel;
import rehabilitation.api.service.entity.ReHubModel;
import rehabilitation.api.service.entity.SpecialistModel;
import rehabilitation.api.service.exceptionHandling.exception.AlreadyExistLoginException;
import rehabilitation.api.service.exceptionHandling.exception.NotFoundLoginException;
import rehabilitation.api.service.exceptionHandling.exception.NullLoginException;
import rehabilitation.api.service.repositories.ClientRepository;
import rehabilitation.api.service.repositories.ReHubRepository;
import rehabilitation.api.service.repositories.SpecialistRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReHubService extends CommonService<ReHubModel, RehubDto>{
    private final ReHubRepository reHubRepository;
    private final SpecialistRepository specialistRepository;

//
//    @Autowired
//    public void setReHubRepository(ReHubRepository reHubRepository) {
//        this.reHubRepository = reHubRepository;
//    }
//
//    @Autowired
//    public void setSpecialistRepository(SpecialistRepository specialistRepository) {
//        this.specialistRepository = specialistRepository;
//    }

    @Override
    @Transactional(readOnly = true)
    public List<RehubDto> getAllModelView() {
        List<ReHubModel> reHubModels = reHubRepository.findAllBy();
        return reHubModels.stream().map(reModel -> {
            List<String> listOfSpecialistsLogin = reModel.getSpecialists()
                    .stream()
                    .map(SpecialistModel::getLogin)
                    .toList();


            return doMapModelDtoAndGet(reModel, listOfSpecialistsLogin);
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RehubDto getModelViewByLogin(String login) throws NotFoundLoginException {
        var reModel = checkIfBaseHasLogin(login, reHubRepository);
        List<String> listOfClientsLogin = reModel.getSpecialists()
                .stream().map(SpecialistModel::getLogin)
                .collect(Collectors.toList());
        return doMapModelDtoAndGet(reModel, listOfClientsLogin);
    }

    @Override
    @Transactional
    public void saveModel(ReHubModel reHubModel) throws AlreadyExistLoginException {
        // todo check if exists injected specialists
        if (checkIfBaseHasModel(reHubModel, reHubRepository)) {
            var specialistsCopy = new HashSet<SpecialistModel>();

            reHubModel.getSpecialists()
                            .forEach(specialistModel -> {
                                try {
                                    var realSpecialist = checkIfBaseHasLogin(specialistModel.getLogin(), specialistRepository);
                                    realSpecialist.setReHub(reHubModel);
                                    specialistsCopy.add(realSpecialist);
                                } catch (NotFoundLoginException e) {
                                    throw new RuntimeException(e);
                                }
                            });
            reHubModel.setSpecialists(specialistsCopy);
            reHubRepository.save(reHubModel);
        }
    }

    @Override
    public void deleteModel(String login) throws NotFoundLoginException {
        var reHubModel = checkIfBaseHasLogin(login, reHubRepository);
        reHubRepository.delete(reHubModel);
    }

    @Override
    @Transactional
    public void addChild(String reHubLogin, String specialistLogin) throws NotFoundLoginException {
        var reHub = checkIfBaseHasLogin(reHubLogin, reHubRepository);
        var specialist = checkIfBaseHasLogin(specialistLogin, specialistRepository);
        reHub.addSpecialist(specialist);
    }

    @Override
    public void removeChild(String reHubLogin, String specialistLogin) throws NotFoundLoginException {
        var reHub = checkIfBaseHasLogin(reHubLogin, reHubRepository);
        var specialist = checkIfBaseHasLogin(specialistLogin, specialistRepository);
        reHub.removeSpecialist(specialist);
    }

    @Override
    RehubDto doMapModelDtoAndGet(ReHubModel reHubModel, List<String> listOfSpecialistLogin) {
        return new RehubDto(
                reHubModel.getLogin(), reHubModel.getFirstName(),
                reHubModel.getEmail(), reHubModel.getAddress(),
                reHubModel.getContactInformation(), reHubModel.getImgUrl(),
                listOfSpecialistLogin);
    }

    @Override
    void executeUpdates(Map<String, Object> updates, ReHubModel currentReHub) {
        updates.forEach((key, value) -> {
            switch (key) {
                case "firstName":
                    if (value instanceof String) {
                        currentReHub.setFirstName((String) value);
                    }
                    break;
                case "address":
                    if (value instanceof String) {
                        currentReHub.setAddress((String) value);
                    }
                    break;
                case "contactInformation":
                    if (value instanceof String) {
                        currentReHub.setContactInformation((String) value);
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
        var currentClient = checkIfBaseHasLogin(login, reHubRepository);
        executeUpdates(updates, currentClient);
    }
}
