package rehabilitation.api.service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rehabilitation.api.service.entity.ClientModel;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<ClientModel, String> {

    // todo its important to fetch child objects to avoid extra queries to db
    @Query("select c from ClientModel c left join fetch c.specialists")
    List<ClientModel> findAllBy();
    @Query("select c from ClientModel c left join fetch c.specialists where c.login=:login")
    ClientModel findByLogin(@Param("login") String login);

    ClientModel findFirstByAddress(String address);
}
