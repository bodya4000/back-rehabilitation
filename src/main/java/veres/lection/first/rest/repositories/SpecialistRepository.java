package veres.lection.first.rest.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import veres.lection.first.rest.model.SpecialistModel;

@Repository
public interface SpecialistRepository extends JpaRepository<SpecialistModel, Integer> {

}
