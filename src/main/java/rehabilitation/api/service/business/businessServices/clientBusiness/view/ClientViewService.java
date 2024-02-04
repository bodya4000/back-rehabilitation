package rehabilitation.api.service.business.businessServices.clientBusiness.view;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rehabilitation.api.service.business.businessServices.abstractions.ModelViewService;
import rehabilitation.api.service.business.businessUtils.MappingUtil;
import rehabilitation.api.service.dto.entities.ClientDto;
import rehabilitation.api.service.entity.sql.ClientModel;
import rehabilitation.api.service.exceptionHandling.exception.buisness.NotFoundLoginException;
import rehabilitation.api.service.repositories.jpa.ClientRepository;

import java.util.List;
import java.util.stream.Collectors;

import static rehabilitation.api.service.business.businessUtils.ModelValidationUtils.getModelIfExists;

@Service
@RequiredArgsConstructor
public class ClientViewService extends ModelViewService<ClientDto> {

    private final ClientRepository clientRepository;
    private final MappingUtil mappingUtil;

    @Override
    @Transactional(readOnly = true)
    public ClientDto getModelDtoByLogin(String login) throws NotFoundLoginException {
        var clientModel = getModelIfExists(login, clientRepository);
        return mappingUtil.doMapClientDtoAndGet(clientModel);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientDto> getListOfModelDto() {
        List<ClientModel> clientModels = clientRepository.findAllBy();
        return clientModels.stream().map(mappingUtil::doMapClientDtoAndGet).collect(Collectors.toList());
    }

}
