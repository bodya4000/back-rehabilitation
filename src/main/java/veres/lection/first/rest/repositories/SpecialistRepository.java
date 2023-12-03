package veres.lection.first.rest.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import veres.lection.first.rest.model.SpecialistModel;

import java.util.List;

@Repository
public interface SpecialistRepository extends JpaRepository<SpecialistModel, Integer> {

//    public List<Integer> getListOfClientsId(SpecialistModel specialistModel);
}
