package rehabilitation.api.service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rehabilitation.api.service.entity.ClientModel;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<ClientModel, String>,CommonRepository<ClientModel> {

    // todo its important to fetch child objects to avoid extra queries to db
    @Override
    @Query("select c from ClientModel c left join fetch c.specialists")
    List<ClientModel> findAllBy();

    @Override
    @Query("select c from ClientModel c left join fetch c.specialists where c.login=:login")
    Optional<ClientModel> findByLogin(@Param("login") String login);


}
