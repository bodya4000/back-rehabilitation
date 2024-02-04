package rehabilitation.api.service.repositories.elasticsearch;

import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Repository;
import rehabilitation.api.service.entity.sql.SpecialistModel;

@Repository
public class SpecialistSearchRepositoryCustomImpl implements SpecialistSearchRepositoryCustom {

    private final ElasticsearchOperations elasticsearchOperations;

    public SpecialistSearchRepositoryCustomImpl(ElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = elasticsearchOperations;
    }

//    @Override
//    public List<SpecialistModel> findSpecialistByText(String text) {
//        Query query = NativeQuery.builder()
//                .withQuery(q -> q
//                        .match(m -> m
//                                .field("firstName")
//                                .query(text)))
//                .withQuery(q -> q
//                        .match(m -> m
//                                .field("lastName")
//                                .query(text)))
//                .withQuery(q -> q
//                        .match(m -> m
//                                .field("login")
//                                .query(text)))
//                .build();
//        SearchHits<SpecialistModel> searchHits = elasticsearchOperations.search(query, SpecialistModel.class);
//        return searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
//    }

    @Override
    public void saveSpecialist(SpecialistModel specialistModel) {
        elasticsearchOperations.save(specialistModel);
    }


}