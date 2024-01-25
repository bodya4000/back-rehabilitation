package rehabilitation.api.service.business.businessServices.specialistBusiness.search;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.FuzzyQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import rehabilitation.api.service.dto.SearchDto;

import java.util.function.Supplier;

public class QueryBuilderUtil {

    public static Query searchQuery(SearchDto searchDto) {
        return BoolQuery.of(builder1 -> builder1
                .should(QueryBuilderUtil.createFuzzyQuery("login", searchDto.searchText())._toQuery())
                .should(QueryBuilderUtil.createFuzzyQuery("firstName", searchDto.searchText())._toQuery())
                .should(QueryBuilderUtil.createFuzzyQuery("lastName", searchDto.searchText())._toQuery())
                .should(builder2 ->
                        builder2
                                .match(m -> m
                                        .field("city")
                                        .query(searchDto.city())))
                .should(builder2 ->
                        builder2
                                .match(m -> m
                                        .field("speciality")
                                        .query(searchDto.speciality()))))
                ._toQuery();
    }



    private static FuzzyQuery createFuzzyQuery(String field, String value) {
        var fuzzyQuery = new FuzzyQuery.Builder();
        return fuzzyQuery
                .field(field)
                .value(value)
                .build();
    }


}
