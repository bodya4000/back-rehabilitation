package rehabilitation.api.service.repositories;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rehabilitation.api.service.entity.BaseModel;

import java.util.List;
import java.util.Optional;

public interface CommonRepository<AnyModel extends BaseModel> {
    List<AnyModel> findAllBy();
    Optional<AnyModel> findByLogin(@Param("login") String login);
    boolean existsByLogin(String login);

    boolean existsByEmail(String email);




}


