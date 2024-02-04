package rehabilitation.api.service.repositories.elasticsearch;

import rehabilitation.api.service.entity.sql.SpecialistModel;

public interface SpecialistSearchRepositoryCustom {

    void saveSpecialist(SpecialistModel specialistModel);
}
