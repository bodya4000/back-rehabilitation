package rehabilitation.api.service.repositories.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rehabilitation.api.service.entity.sql.SpecialistModel;

import java.util.List;
import java.util.Optional;

public interface SpecialistRepository extends JpaRepository<SpecialistModel, Long>, CommonRepository<SpecialistModel> {

    @Override
    @Query("select s from SpecialistModel s " +
            "left join fetch s.clients " +
            "left join fetch s.reHub ")
    List<SpecialistModel> findAllBy();

    @Override
    @Query("select s from SpecialistModel s " +
            "left join fetch s.clients " +
            "left join fetch s.reHub " +
            "where s.login = :login")
    Optional<SpecialistModel> findByLogin(@Param("login") String login);


    @Override
    @Query("select distinct s from SpecialistModel s left join fetch s.roles where s.login=:login")
    Optional<SpecialistModel> findByLoginFetchRoles(String login);

    Optional<List<SpecialistModel>> findByCityAndAgeAndSpecialityContainingAllIgnoreCase
            (String city, Integer age, String speciality);

}