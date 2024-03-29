package rehabilitation.api.service.repositories.jpa;

import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommonRepository<User> {
    List<User> findAllBy();

    Optional<User> findByLogin(@Param("login") String login);

    Optional<User> findByLoginFetchRoles(@Param("login") String login);

    boolean existsByLogin(String login);

    boolean existsByEmail(String email);
}
