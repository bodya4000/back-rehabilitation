package rehabilitation.api.service.business.businessServices.specialistBusiness.search;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.stereotype.Service;
import rehabilitation.api.service.business.businessUtils.MappingUtil;
import rehabilitation.api.service.dto.SearchDto;
import rehabilitation.api.service.dto.entities.SpecialistDto;
import rehabilitation.api.service.entity.SpecialistModel;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class SearchSpecialistService {

    private final ElasticsearchClient elasticsearchClient;
    private final MappingUtil mappingUtil;

    public List<SpecialistDto> getSearchedSpecialists(SearchDto searchDto) throws IOException {
        Query query = QueryBuilderUtil.searchQuery(searchDto);
        SearchRequest searchRequest = makeSearchRequest(query);
        return getSpecialistDtosFromSearchResponse(searchRequest);
    }

    private SearchRequest makeSearchRequest(Query findByLoginQuery) {
        return new SearchRequest.Builder()
                .query(findByLoginQuery)
                .size(10)
                .build();
    }

    private List<SpecialistDto> getSpecialistDtosFromSearchResponse(SearchRequest searchRequest) throws IOException {
        return elasticsearchClient.search(searchRequest, SpecialistModel.class)
                .hits()
                .hits()
                .stream().map(Hit::source).filter(Objects::nonNull)
                .map(mappingUtil::doMapSpecialistDtoAndGet)
                .toList();
    }


}
