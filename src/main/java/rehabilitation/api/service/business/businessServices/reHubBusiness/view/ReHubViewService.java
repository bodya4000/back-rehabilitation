package rehabilitation.api.service.business.businessServices.reHubBusiness.view;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rehabilitation.api.service.business.businessServices.abstractions.ModelViewService;
import rehabilitation.api.service.business.businessUtils.MappingUtil;
import rehabilitation.api.service.dto.entities.RehubDto;
import rehabilitation.api.service.entity.sql.ReHubModel;
import rehabilitation.api.service.exceptionHandling.exception.buisness.NotFoundLoginException;
import rehabilitation.api.service.repositories.jpa.ReHubRepository;

import java.util.List;
import java.util.stream.Collectors;

import static rehabilitation.api.service.business.businessUtils.ModelValidationUtils.getModelIfExists;

@Service
@RequiredArgsConstructor
public class ReHubViewService extends ModelViewService<RehubDto> {

    private final ReHubRepository reHubRepository;
    private final MappingUtil mappingUtil;

    @Override
    @Transactional(readOnly = true)
    public RehubDto getModelDtoByLogin(String login) throws NotFoundLoginException {
        var reHubModel = getModelIfExists(login, reHubRepository);
        return mappingUtil.doMapReHubDtoAndGet(reHubModel);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RehubDto> getListOfModelDto() {
        List<ReHubModel> reHubModels = reHubRepository.findAllBy();
        return reHubModels.stream().map(mappingUtil::doMapReHubDtoAndGet).collect(Collectors.toList());
    }

}
