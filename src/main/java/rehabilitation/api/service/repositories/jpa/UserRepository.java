package rehabilitation.api.service.repositories.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rehabilitation.api.service.dto.entities.UserDto;
import rehabilitation.api.service.entity.sql.UserModel;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserModel, String>, CommonRepository<UserModel> {

    @Override
    List<UserModel> findAllBy();

    @Override
    Optional<UserModel> findByLogin(@Param("login") String login);

    @Query("select distinct new rehabilitation.api.service.dto.entities.UserDto(" +
            "u.login, u.email, u.contactInformation, " +
            "u.address, u.imgUrl) " +
            "from UserModel u " +
            "where u.login = :login")
    Optional<UserDto> findDtoByLogin(String login);


    @Override
    @Query("select distinct u from UserModel u left join fetch u.roles where u.login=:login")
    Optional<UserModel> findByLoginFetchRoles(String login);
}


