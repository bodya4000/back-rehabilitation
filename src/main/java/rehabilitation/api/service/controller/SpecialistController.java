package rehabilitation.api.service.controller;

import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rehabilitation.api.service.business.businessServices.specialistBusiness.SpecialistService;
import rehabilitation.api.service.business.businessServices.specialistBusiness.search.SearchSpecialistService;
import rehabilitation.api.service.dto.SearchDto;
import rehabilitation.api.service.dto.entities.SpecialistDto;
import rehabilitation.api.service.exceptionHandling.exception.buisness.NotFoundLoginException;

import java.io.IOException;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/specialist-section")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class SpecialistController {

    private final SpecialistService specialistService;
    private final SearchSpecialistService searchSpecialistService;

    @GetMapping("/specialist")
    public List<SpecialistDto> getAllSpecialists() {
        return specialistService.getAllModelView();
    }


    @GetMapping("/specialist/{login}")
    public SpecialistDto getSpecialistById(@PathVariable("login") String login) throws NotFoundLoginException {
        return specialistService.getModelDtoByLogin(login);
    }

    @PatchMapping("/specialist/{login}")
    @PreAuthorize("#login == authentication.principal")
    public ResponseEntity<String> changeSpecialist(
            @PathVariable("login") String login,
            @RequestBody Map<String, Object> updates) throws NotFoundLoginException {
        specialistService.updateModel(login, updates);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("specialist successfully updated");
    }

    @DeleteMapping("/specialist/{login}")
    @RolesAllowed("ADMIN")
    public ResponseEntity<String> deleteSpecialist(@PathVariable("login") String login) throws NotFoundLoginException {
        specialistService.deleteModel(login);
        return ResponseEntity.ok("specialist deleted");
    }

    @PostMapping("/{specialistLogin}/client/{clientLogin}")
    @PreAuthorize("#specialistLogin == authentication.principal")
    public ResponseEntity<Integer> addClient(
            @PathVariable("specialistLogin") String specialistLogin,
            @PathVariable("clientLogin") String clientLogin) throws NotFoundLoginException {
        specialistService.addClient(specialistLogin, clientLogin);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{specialistLogin}/client/{clientLogin}")
    @PreAuthorize("#specialistLogin == authentication.principal")
    public ResponseEntity<Integer> removeClient(
            @PathVariable("clientLogin") String clientLogin,
            @PathVariable("specialistLogin") String specialistLogin) throws NotFoundLoginException {
        specialistService.removeClient(specialistLogin, clientLogin);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping
    public ResponseEntity<List<SpecialistDto>> getSpecialistBySearch(@RequestBody SearchDto searchDto) throws IOException {
        return ResponseEntity.ok(searchSpecialistService.getSearchedSpecialists(searchDto));
    }

}
