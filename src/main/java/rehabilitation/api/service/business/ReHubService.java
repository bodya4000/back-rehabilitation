package rehabilitation.api.service.business;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rehabilitation.api.service.dto.ClientDto;
import rehabilitation.api.service.dto.RegistrationDto;
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
import rehabilitation.api.service.repositories.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReHubService extends CommonService<ReHubModel, RehubDto>{
    private final ReHubRepository reHubRepository;
    private final SpecialistRepository specialistRepository;


    @Override
    @Transactional(readOnly = true)
    public List<RehubDto> getAllModelView() {
        List<ReHubModel> reHubModels = reHubRepository.findAllBy();
        return reHubModels.stream().map(reModel -> {
            List<String> listOfSpecialistsLogin = reModel.getListOfSpecialistsLogin();
            return doMapModelDtoAndGet(reModel, listOfSpecialistsLogin);
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "reHubs", key = "#login", unless = "#result == null ")
    public RehubDto getModelViewByLogin(String login) throws NotFoundLoginException {
        var reModel = getModelIfExists(login, reHubRepository);
        List<String> listOfClientsLogin = reModel.getSpecialists()
                .stream().map(SpecialistModel::getLogin)
                .collect(Collectors.toList());
        return doMapModelDtoAndGet(reModel, listOfClientsLogin);
    }


    @Override
    @CacheEvict(value = "reHubs", key = "#login")
    @Transactional
    public void deleteModel(String login) throws NotFoundLoginException {
        var reHubModel = getModelIfExists(login, reHubRepository);
        reHubRepository.delete(reHubModel);
    }

    @Override
    @Transactional
    @Caching( evict = {
            @CacheEvict(value = "reHubs", key = "#reHubLogin"),
            @CacheEvict(value = "specialists", key = "#specialistLogin")})
    public void addChild(String reHubLogin, String specialistLogin) throws NotFoundLoginException {
        var reHub = getModelIfExists(reHubLogin, reHubRepository);
        var specialist = getModelIfExists(specialistLogin, specialistRepository);
        reHub.addSpecialist(specialist);
    }

    @Override
    @Transactional
    @Caching( evict = {
            @CacheEvict(value = "reHubs", key = "#reHubLogin"),
            @CacheEvict(value = "specialists", key = "#specialistLogin")})
    public void removeChild(String reHubLogin, String specialistLogin) throws NotFoundLoginException {
        var reHub = getModelIfExists(reHubLogin, reHubRepository);
        var specialist = getModelIfExists(specialistLogin, specialistRepository);
        reHub.removeSpecialist(specialist);
    }

    @Override
    RehubDto doMapModelDtoAndGet(ReHubModel reHubModel, List<String> listOfSpecialistLogin) {
        return new RehubDto(
                reHubModel.getLogin(), reHubModel.getName(),
                reHubModel.getEmail(), reHubModel.getAddress(),
                reHubModel.getContactInformation(), reHubModel.getImgUrl(),
                listOfSpecialistLogin);
    }


    @Override
    void executeUpdates(Map<String, Object> updates, ReHubModel currentReHub) {
        updates.forEach((key, value) -> {
            switch (key) {
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
    @CacheEvict(value = "reHubs", key = "#login")
    public void updateModel(String login, Map<String, Object> updates) throws NotFoundLoginException {
        var currentClient = getModelIfExists(login, reHubRepository);
        executeUpdates(updates, currentClient);
    }

}

