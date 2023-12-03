package veres.lection.first.rest.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import veres.lection.first.rest.business.ClientService;
import veres.lection.first.rest.business.SpecialistService;
import veres.lection.first.rest.exception.NotFoundIdException;
import veres.lection.first.rest.model.ClientModel;
import veres.lection.first.rest.model.SpecialistModel;
import veres.lection.first.rest.repositories.ClientRepository;
import veres.lection.first.rest.repositories.SpecialistRepository;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/specialist-section")
public class SpecialistController {

    @Autowired
    private SpecialistService specialistService;

    @Autowired
    private ClientService clientService;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private SpecialistRepository specialistRepository;


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
    public SpecialistModel getSpecialistById(@PathVariable("id") int id) throws NotFoundIdException {
        return specialistService.getById(id);
    }

    /*
     * This method creates a specialist in database and returns its json
     * */
    @PostMapping("/specialist")
    public ResponseEntity<String> createSpecialist(@RequestBody SpecialistModel specialist) throws NotFoundIdException {
        // Виклик сервісу для збереження спеціаліста в базі даних
        for(ClientModel client: specialist.getClients()) {
            ClientModel newClient = clientService.getClientById(client.getId());
            newClient.addSpecialist(specialist);
            clientService.addSpecialist(newClient, specialist);
        }
        // Збереження спеціаліста в базі даних
        specialistRepository.save(specialist);
        return ResponseEntity.ok("Specialist created successfully.");
    }


    /*
     * This method updates a specialist in database and returns its json
     * */
    @PatchMapping("/specialist/{id}")
    public SpecialistModel changeSpecialist(@PathVariable("id") int id, @RequestBody Map<String, Object> updates) throws NotFoundIdException {
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


    @PostMapping("/{specialistId}/client/{clientId}")
    public ResponseEntity<Integer> addNewClient(@PathVariable("specialistId") int specialistId, @PathVariable("clientId") int clientId) throws NotFoundIdException {
        var specialist = specialistService.getById(specialistId);
        var client = clientService.getClientById(clientId);
        specialistService.addClientById(specialist, client);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{specialistId}/client/{clientId}")
    public ResponseEntity<Integer> removeNewClient(@PathVariable("clientId") int clientId, @PathVariable("specialistId") int specialistId) throws NotFoundIdException {
        var specialist = specialistService.getById(specialistId);
        var client = clientService.getClientById(clientId);
        specialistService.removeClientById(specialist, client);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
