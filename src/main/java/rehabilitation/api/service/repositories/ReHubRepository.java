package rehabilitation.api.service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rehabilitation.api.service.entity.ReHubModel;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReHubRepository extends JpaRepository<ReHubModel, String> {

    @Query("select distinct r from ReHubModel r " +
            "left join fetch r.specialists s " +
            "left join fetch s.clients")
    Optional<List<ReHubModel>> findAllBy();


    @Query("select distinct r from ReHubModel r " +
            "left join fetch r.specialists s " +
            "left join fetch s.clients " +
            "where r.login=:login")
    Optional<ReHubModel> findByLogin(@Param("login") String login);
}
