package rehabilitation.api.service.business.businessServices.specialistBusiness;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rehabilitation.api.service.business.businessServices.specialistBusiness.crud.SpecialistCrudService;
import rehabilitation.api.service.business.businessServices.specialistBusiness.view.SpecialistViewService;
import rehabilitation.api.service.dto.entities.SpecialistDto;
import rehabilitation.api.service.exceptionHandling.exception.buisness.NotFoundLoginException;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SpecialistService{

    private final SpecialistCrudService specialistCrudService;
    private final SpecialistViewService specialistViewService;

    @Transactional(readOnly = true)
    @Cacheable(value = "specialists", key = "#login")
    public SpecialistDto getModelDtoByLogin(String login) throws NotFoundLoginException {
        return specialistViewService.getModelDtoByLogin(login);
    }

    @Transactional(readOnly = true)
    public List<SpecialistDto> getAllModelView() {
        return specialistViewService.getListOfModelDto();
    }

    @Transactional
    @CacheEvict(value = "specialists", key = "#login")
    public void updateModel(String login, Map<String, Object> updates) throws NotFoundLoginException {
        specialistCrudService.updateModel(login, updates);
    }

    @Transactional
    @CacheEvict(value = "specialists", key = "#login")
    public void deleteModel(String login) throws NotFoundLoginException {
        specialistCrudService.deleteModel(login);
    }

    @Transactional
    @Caching( evict = {
            @CacheEvict(value = "specialists", key = "#specialistLogin"),
            @CacheEvict(value = "clients", key = "#clientLogin"),})
    public void addClient(String specialistLogin, String clientLogin) throws NotFoundLoginException {
        specialistCrudService.addClient(specialistLogin, clientLogin);
    }

    @Transactional
    @Caching( evict = {
            @CacheEvict(value = "specialists", key = "#specialistLogin"),
            @CacheEvict(value = "clients", key = "#clientLogin"),})
    public void removeClient(String specialistLogin, String clientLogin) throws NotFoundLoginException {
        specialistCrudService.removeClient(specialistLogin, clientLogin);
    }


}
