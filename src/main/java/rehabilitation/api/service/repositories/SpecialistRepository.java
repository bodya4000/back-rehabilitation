package rehabilitation.api.service.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rehabilitation.api.service.dto.SpecialistDto;
import rehabilitation.api.service.entity.SpecialistModel;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpecialistRepository extends JpaRepository<SpecialistModel, Integer> {

    // todo its important to fetch child objects to avoid extra queries to db
    @Query("select s from SpecialistModel s " +
            "left join fetch s.clients " +
            "left join fetch s.reHub ")
    List<SpecialistModel> getAllBy();

    @Query("select s from SpecialistModel s " +
            "left join fetch s.clients " +
            "left join fetch s.reHub " +
            "where s.login = :login")
    Optional<SpecialistModel> findByLogin(@Param("login") String login);


}
