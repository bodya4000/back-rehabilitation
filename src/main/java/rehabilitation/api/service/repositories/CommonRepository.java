package rehabilitation.api.service.repositories;

import org.springframework.data.repository.query.Param;
import rehabilitation.api.service.entity.CommonModel;

import java.util.List;
import java.util.Optional;

public interface CommonRepository<AnyModel extends CommonModel> {
    List<AnyModel> findAllBy();
    Optional<AnyModel> findByLogin(@Param("login") String login);
    boolean existsByLogin(String login);

    boolean existsByEmail(String email);




}


