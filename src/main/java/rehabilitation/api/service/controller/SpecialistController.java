package rehabilitation.api.service.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rehabilitation.api.service.business.SpecialistService;
import rehabilitation.api.service.dto.SpecialistDto;
import rehabilitation.api.service.entity.SpecialistModel;
import rehabilitation.api.service.repositories.SpecialistRepository;
import rehabilitation.api.service.business.ClientService;
import rehabilitation.api.service.exception.NotFoundIdException;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/specialist-section")
@CrossOrigin(origins = "http://localhost:3000")
public class SpecialistController {

    @Autowired
    private SpecialistService specialistService;

    @Autowired
    private ClientService clientService;

    /*
     * This method returns all specialists from database
     * */
    @GetMapping("/specialist")
    public List<SpecialistDto> getAllBy() {
        return specialistService.getAllSpecialistView();
    }

    /*
     * This method returns a specialist by login from database
     * */

    @GetMapping("/specialist/{login}")
    public SpecialistDto getSpecialistById(@PathVariable("login") String login) throws NotFoundIdException {
        return specialistService.getSpecialistView(login);
    }

    /*
     * This method creates a specialist in database and returns its json
     * */

    @PostMapping("/specialist")
    public ResponseEntity<String> createSpecialist(@RequestBody SpecialistModel specialist) throws NotFoundIdException {
        specialistService.saveSpecialist(specialist);
        return ResponseEntity.ok("Specialist created successfully.");
    }


    /*
     * This method updates a specialist in database and returns its json
     * */
    @PatchMapping("/specialist/{login}")
    public ResponseEntity<String> changeSpecialist(@PathVariable("login") String login, @RequestBody Map<String, Object> updates) throws NotFoundIdException {
        specialistService.updateSpecialist(login, updates);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("specialist successfully updated");
    }

    /*
     * This method removes a specialist in database
     * */
    @DeleteMapping("/specialist/{login}")
    public void deleteSpecialist(@PathVariable("login") String login) throws NotFoundIdException {
        specialistService.deleteSpecialist(login);
    }


    @PostMapping("/{specialistLogin}/client/{clientLogin}")
    public ResponseEntity<Integer> addNewClient(@PathVariable("specialistLogin") String specialistLogin, @PathVariable("clientLogin") String clientLogin) throws NotFoundIdException {
        specialistService.addClient(specialistLogin, clientLogin);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{specialistLogin}/client/{clientLogin}")
    public ResponseEntity<Integer> removeNewClient(@PathVariable("clientLogin") String clientLogin, @PathVariable("specialistLogin") String specialistLogin) throws NotFoundIdException {
        specialistService.removeClient(specialistLogin, clientLogin);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
