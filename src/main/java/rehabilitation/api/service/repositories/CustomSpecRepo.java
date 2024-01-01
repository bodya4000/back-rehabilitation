package rehabilitation.api.service.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import rehabilitation.api.service.dto.SpecialistDto;
import rehabilitation.api.service.entity.SpecialistModel;


public interface CustomSpecRepo extends CommonRepository<SpecialistModel> {
    SpecialistDto testDto(String login);

}
