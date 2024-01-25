package rehabilitation.api.service.repositories.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import rehabilitation.api.service.entity.SpecialistModel;

@Repository
public interface SpecialistSearchRepository extends ElasticsearchRepository<SpecialistModel, String>, SpecialistSearchRepositoryCustom {
}