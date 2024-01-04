package rehabilitation.api.service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rehabilitation.api.service.business.SpecialistService;
import rehabilitation.api.service.dto.RegistrationDto;
import rehabilitation.api.service.dto.SpecialistDto;
import rehabilitation.api.service.entity.SpecialistModel;
import rehabilitation.api.service.business.ClientService;
import rehabilitation.api.service.exceptionHandling.exception.AlreadyExistLoginException;
import rehabilitation.api.service.exceptionHandling.exception.NotFoundLoginException;
import rehabilitation.api.service.repositories.SpecialistRepository;
import rehabilitation.api.service.repositories.SpecialistRepositoryImpl;

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

    @Autowired
    private SpecialistRepository specialistRepository;

    /*
     * This method returns all specialists from database
     * */
    @GetMapping("/specialist")
    public List<SpecialistDto> getAllBy() {
        return specialistService.getAllModelView();
    }

    /*
     * This method returns a specialist by login from database
     * */

    @GetMapping("/specialist/{login}")
    public SpecialistDto getSpecialistById(@PathVariable("login") String login) throws NotFoundLoginException {
        return specialistService.getModelViewByLogin(login);
    }

    /*
     * This method creates a specialist in database and returns its json
     * */

    @PostMapping("/specialist")
    public ResponseEntity<String> createSpecialist(@RequestBody RegistrationDto registrationDto) throws NotFoundLoginException, AlreadyExistLoginException {
        specialistService.signUpModel(registrationDto);
        return ResponseEntity.ok("Specialist created successfully.");
    }

    /*
     * This method updates a specialist in database and returns its json
     * */

    @PatchMapping("/specialist/{login}")
    public ResponseEntity<String> changeSpecialist(@PathVariable("login") String login, @RequestBody Map<String, Object> updates) throws NotFoundLoginException {
        specialistService.updateModel(login, updates);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("specialist successfully updated");
    }

    /*
     * This method removes a specialist in database
     * */
    @DeleteMapping("/specialist/{login}")
    public void deleteSpecialist(@PathVariable("login") String login) throws NotFoundLoginException {
        specialistService.deleteModel(login);
    }


    @PostMapping("/{specialistLogin}/client/{clientLogin}")
    public ResponseEntity<Integer> addNewClient(@PathVariable("specialistLogin") String specialistLogin, @PathVariable("clientLogin") String clientLogin) throws NotFoundLoginException {
        specialistService.addChild(specialistLogin, clientLogin);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{specialistLogin}/client/{clientLogin}")
    public ResponseEntity<Integer> removeNewClient(@PathVariable("clientLogin") String clientLogin, @PathVariable("specialistLogin") String specialistLogin) throws NotFoundLoginException {
        specialistService.removeChild(specialistLogin, clientLogin);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/test/{login}/dto")
    public SpecialistDto testDto(@PathVariable("login") String login) throws NotFoundLoginException {
        return specialistRepository.testDto(login);
    }
}
