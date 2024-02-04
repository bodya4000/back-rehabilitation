package rehabilitation.api.service.business.businessServices.reHubBusiness;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rehabilitation.api.service.business.businessServices.reHubBusiness.crud.ReHubCrudService;
import rehabilitation.api.service.business.businessServices.reHubBusiness.view.ReHubViewService;
import rehabilitation.api.service.dto.entities.RehubDto;
import rehabilitation.api.service.exceptionHandling.exception.buisness.NotFoundLoginException;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReHubService {
    private final ReHubViewService reHubViewService;
    private final ReHubCrudService reHubCrudService;


    @Transactional(readOnly = true)
    public List<RehubDto> getAllModelView() {
        return reHubViewService.getListOfModelDto();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "reHubs", key = "#login", unless = "#result == null ")
    public RehubDto getModelDtoByLogin(String login) throws NotFoundLoginException {
        return reHubViewService.getModelDtoByLogin(login);
    }


    @CacheEvict(value = "reHubs", key = "#login")
    @Transactional
    public void deleteModel(String login) throws NotFoundLoginException {
        reHubCrudService.deleteModel(login);
    }

    @Transactional
    @Caching( evict = {
            @CacheEvict(value = "reHubs", key = "#reHubLogin"),
            @CacheEvict(value = "specialists", key = "#specialistLogin")})
    public void addSpecialist(String reHubLogin, String specialistLogin) throws NotFoundLoginException {
        reHubCrudService.addSpecialist(reHubLogin, specialistLogin);
    }

    @Transactional
    @Caching( evict = {
            @CacheEvict(value = "reHubs", key = "#reHubLogin"),
            @CacheEvict(value = "specialists", key = "#specialistLogin")})
    public void removeSpecialist(String reHubLogin, String specialistLogin) throws NotFoundLoginException {
        reHubCrudService.removeSpecialist(reHubLogin, specialistLogin);
    }

    @Transactional
    @CacheEvict(value = "reHubs", key = "#login")
    public void updateModel(String login, Map<String, Object> updates) throws NotFoundLoginException {
        reHubCrudService.updateModel(login, updates);
    }
}

