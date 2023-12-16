package rehabilitation.api.service.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rehabilitation.api.service.dto.ClientDto;
import rehabilitation.api.service.entity.ReHubModel;
import rehabilitation.api.service.repositories.ClientRepository;
import rehabilitation.api.service.business.ClientService;
import rehabilitation.api.service.business.SpecialistService;
import rehabilitation.api.service.exception.NotFoundIdException;
import rehabilitation.api.service.entity.ClientModel;
import rehabilitation.api.service.repositories.SpecialistRepository;

import java.util.*;

@RestController
@RequestMapping("/client-section")
@CrossOrigin(origins = "http://localhost:3000")
public class ClientController {

    @Autowired
    private ClientService clientService;

    /*
     * This method returns all clients from database
     * */

    @GetMapping("/client")
    public List<ClientDto> getClients() {
        return clientService.getAllClientsView();
    }

    /*
     * This method returns a client by login from database
     * */

    @GetMapping("/client/{login}")
    public ClientDto getClientById(@PathVariable("login") String login) throws NotFoundIdException {
        return clientService.getClientById(login);
    }

    /*
     * This method create a client in database and returns its json
     * */

    @PostMapping(value = "/client")
    public ResponseEntity<String> createClient(@RequestBody ClientModel clientModel) {
        clientService.saveClient(clientModel);
        return ResponseEntity.status(HttpStatus.CREATED).body("client successfully saved");
    }

    /*
     * This method updates a client by login in database and returns its json
     * */

    @PatchMapping("/client/{login}")
    public ResponseEntity<String> updateClient(@PathVariable("login") String login, @RequestBody Map<String, Object> updates) throws NotFoundIdException {
        clientService.updateClient(login, updates);
        return ResponseEntity.status(HttpStatus.OK).body("client updated");
    }

    /*
     * This method deletes a client by login in database
     * */

    @DeleteMapping("/client/{login}")
    public ResponseEntity<String> deleteClient(@PathVariable("login") String login) throws NotFoundIdException {
        clientService.deleteClient(login);
        return ResponseEntity.status(HttpStatus.OK).body("client deleted");
    }

    @PostMapping("/{clientLogin}/specialist/{specialistLogin}")
    public ResponseEntity<Integer> addSpecialist(@PathVariable("clientLogin") String clientLogin, @PathVariable("specialistLogin") String specialistLogin) throws NotFoundIdException {
        clientService.addSpecialist(clientLogin, specialistLogin);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    //
    @DeleteMapping("/{clientLogin}/specialist/{specialistLogin}")
    public ResponseEntity<Integer> removeSpecialist(@PathVariable("clientLogin") String clientLogin, @PathVariable("specialistLogin") String specialistLogin) throws NotFoundIdException {
        clientService.removeSpecialistById(clientLogin, specialistLogin);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
