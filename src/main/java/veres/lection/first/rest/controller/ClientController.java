package veres.lection.first.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

@RestController
@RequestMapping("/client-section")
public class ClientController {

    @Autowired
    private ClientService clientService;
    @Autowired
    private SpecialistService specialistService;

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private SpecialistRepository specialistRepository;

    /*
     * This method returns all clients from database
     * */

    @GetMapping("/client")
    public List<ClientModel> getClients() {
        var list = clientService.getClientsList();
        return list;
    }

    /*
     * This method returns a client by id from database
     * */

    @GetMapping("/client/{id}")
    public ClientModel getClientById(@PathVariable("id") int id) throws NotFoundIdException {
        return clientService.getClientById(id);
    }

    /*
     * This method create a client in database and returns its json
     * */

    @PostMapping(value = "/client")
    public ResponseEntity<ClientModel> createClient(@RequestBody ClientModel clientModel) {

        ClientModel savedClient = clientRepository.save(clientModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedClient);
    }

    /*
     * This method updates a client by id in database and returns its json
     * */

    @PatchMapping("/client/{id}")
    public ClientModel updateClient(@PathVariable("id") int id, @RequestBody Map<String, Object> updates) throws NotFoundIdException {
        clientService.changeClient(id, updates);
        return clientService.getClientById(id);
    }

    /*
     * This method deletes a client by id in database
     * */

    @DeleteMapping("/client/{id}")
    public void deleteClient(@PathVariable("id") int id) throws NotFoundIdException {
        clientService.deleteClient(id);
    }

    @PostMapping("/{clientId}/specialistId/{specialistId}")
    public ResponseEntity<Integer> addNewClient(@PathVariable("specialistId") int specialistId, @PathVariable("clientId") int clientId) throws NotFoundIdException {
        var specialist = specialistService.getById(specialistId);
        var client = clientService.getClientById(clientId);
        clientService.addSpecialist(client, specialist);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{clientId}/specialist/{specialistId}")
    public ResponseEntity<Integer> removeNewClient(@PathVariable("clientId") int clientId, @PathVariable("specialistId") int specialistId) throws NotFoundIdException {
        var specialist = specialistService.getById(specialistId);
        var client = clientService.getClientById(clientId);
        clientService.removeSpecialistById(client, specialist);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
