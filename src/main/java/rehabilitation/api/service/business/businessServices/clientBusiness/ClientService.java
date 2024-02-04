package rehabilitation.api.service.business.businessServices.clientBusiness;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import rehabilitation.api.service.business.businessServices.clientBusiness.crud.ClientCrudService;
import rehabilitation.api.service.business.businessServices.clientBusiness.view.ClientViewService;
import rehabilitation.api.service.dto.entities.ClientDto;

import rehabilitation.api.service.exceptionHandling.exception.buisness.NotFoundLoginException;



import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service("clientService")
public class ClientService {

    private final ClientViewService clientViewService;
    private final ClientCrudService clientCrudService;

    @Transactional(readOnly = true)
    public List<ClientDto> getAllModelView() {
        return clientViewService.getListOfModelDto();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "clients", key = "#login", unless = "#result == null")
    public ClientDto getModelDtoByLogin(String login) throws NotFoundLoginException {
        return clientViewService.getModelDtoByLogin(login);
    }


    @Transactional
    @CacheEvict(value = "clients", key = "#login")
    public void deleteModel(String login) throws NotFoundLoginException {
        clientCrudService.deleteModel(login);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "clients", key = "#clientLogin"),
            @CacheEvict(value = "specialists", key = "#specialistLogin")})
    public void addSpecialist(String clientLogin, String specialistLogin) throws NotFoundLoginException {
        clientCrudService.addSpecialist(clientLogin, specialistLogin);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "clients", key = "#clientLogin"),
            @CacheEvict(value = "specialists", key = "#specialistLogin")})
    public void removeSpecialist(String clientLogin, String specialistLogin) throws NotFoundLoginException {
        clientCrudService.removeSpecialist(clientLogin, specialistLogin);
    }

    @Transactional
    @CacheEvict(value = "clients", key = "#login")
    public void updateModel(String login, Map<String, Object> updates) throws NotFoundLoginException {
        clientCrudService.updateModel(login, updates);
    }
}


