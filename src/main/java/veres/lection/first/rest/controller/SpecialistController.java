package veres.lection.first.rest.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import veres.lection.first.rest.business.SpecialistService;
import veres.lection.first.rest.exception.NotFoundIdException;
import veres.lection.first.rest.model.SpecialistModel;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/specialist-section")
public class SpecialistController {

    @Autowired
    SpecialistService specialistService;


    /*
     * This method returns all specialists from database
     * */
    @GetMapping("/specialist")
    public List<SpecialistModel> getSpecialists() {
        return specialistService.getAllRehabilitationSpecialists();
    }

    /*
     * This method returns a specialist by id from database
     * */
    @GetMapping("/specialist/{id}")
    public SpecialistModel getSpecialistById(
            @PathVariable("id") int id
    ) throws NotFoundIdException {
        return specialistService.getById(id);
    }

    /*
     * This method creates a specialist in database and returns its json
     * */
    @PostMapping("specialist")
    public ResponseEntity<SpecialistModel> addSpecialist(
            @RequestBody SpecialistModel specialistModel) {
//        log.info(String.valueOf(specialistModel));
        specialistService.saveRehabilitationSpecialist(specialistModel);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(specialistModel);
    }

    /*
     * This method updates a specialist in database and returns its json
     * */
    @PatchMapping("/specialist/{id}")
    public SpecialistModel changeSpecialist(@PathVariable("id") int id,
                                            @RequestBody Map<String, Object> updates) throws NotFoundIdException {
        specialistService.updateRehabilitationSpecialist(id, updates);
        return specialistService.getById(id);
    }

    /*
     * This method removes a specialist in database
     * */
    @DeleteMapping("/specialist/{id}")
    public void deleteSpecialist(@PathVariable("id") int id) throws NotFoundIdException {
        specialistService.deleteRehabilitationSpecialists(id);
    }

}
