package rehabilitation.api.service.business.businessServices.specialistBusiness.view;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rehabilitation.api.service.business.businessServices.abstractions.ModelViewService;
import static rehabilitation.api.service.business.businessUtils.ModelValidationUtils.*;

import rehabilitation.api.service.business.businessUtils.MappingUtil;
import rehabilitation.api.service.dto.entities.SpecialistDto;
import rehabilitation.api.service.entity.sql.SpecialistModel;
import rehabilitation.api.service.exceptionHandling.exception.buisness.NotFoundLoginException;
import rehabilitation.api.service.repositories.jpa.SpecialistRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpecialistViewService extends ModelViewService<SpecialistDto> {

    private final SpecialistRepository specialistRepository;
    private final MappingUtil mappingUtil;

    @Override
    public SpecialistDto getModelDtoByLogin(String login) throws NotFoundLoginException {
        var specialistModel = getModelIfExists(login, specialistRepository);
        return mappingUtil.doMapSpecialistDtoAndGet(specialistModel);
    }

    @Override
    public List<SpecialistDto> getListOfModelDto() {
        List<SpecialistModel> specialistModels = specialistRepository.findAllBy();
        return specialistModels.stream().map(mappingUtil::doMapSpecialistDtoAndGet).collect(Collectors.toList());
    }

}
