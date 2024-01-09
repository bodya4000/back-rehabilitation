package rehabilitation.api.service.controller;

import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rehabilitation.api.service.dto.AuthenticateDto;
import rehabilitation.api.service.dto.ClientDto;
import rehabilitation.api.service.business.ClientService;
import rehabilitation.api.service.dto.JwtResponse;
import rehabilitation.api.service.dto.RegistrationDto;
import rehabilitation.api.service.exceptionHandling.exception.*;

import java.util.*;

@RestController
@RequestMapping("/client-section")
@CrossOrigin(origins = "http://localhost:3000")
public class ClientController {

    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }


    /*
     * This method returns all clients from database
     * */

    @GetMapping("/client")
    @RolesAllowed("ADMIN")
    public List<ClientDto> getClients() {
        return clientService.getAllModelView();
    }

    /*
     * This method returns a client by login from database
     * */

    @GetMapping("/client/{login}")
    @PreAuthorize("isAuthenticated()")
    public ClientDto getClientById(@PathVariable("login") String login) throws NotFoundLoginException {
        return clientService.getModelViewByLogin(login);
    }

    /*
     * This method updates a client by login in database and returns its json
     * */


    @PatchMapping("/client/{login}")
    @PreAuthorize("#login == authentication.principal")
    public ResponseEntity<String> updateClient(@PathVariable("login") String login, @RequestBody Map<String, Object> updates) throws NotFoundLoginException {
        clientService.updateModel(login, updates);
        return ResponseEntity.status(HttpStatus.OK).body("client updated");
    }

    /*
     * This method deletes a client by login in database
     * */

    @DeleteMapping("/client/{login}")
    @RolesAllowed("ADMIN")
    @PreAuthorize("#login == authentication.principal.username")
    public ResponseEntity<String> deleteClient(@PathVariable("login") String login) throws NotFoundLoginException {
        clientService.deleteModel(login);
        return ResponseEntity.status(HttpStatus.OK).body("client deleted");
    }



    @PostMapping("/{clientLogin}/specialist/{specialistLogin}")
    @PreAuthorize("#clientLogin == authentication.principal.username")
    public ResponseEntity<Integer> addSpecialist(@PathVariable("clientLogin") String clientLogin, @PathVariable("specialistLogin") String specialistLogin) throws NotFoundLoginException {
        clientService.addChild(clientLogin, specialistLogin);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @DeleteMapping("/{clientLogin}/specialist/{specialistLogin}")
    @PreAuthorize("#clientLogin == authentication.principal.username")
    public ResponseEntity<Integer> removeSpecialist(@PathVariable("clientLogin") String clientLogin, @PathVariable("specialistLogin") String specialistLogin) throws NotFoundLoginException {
        clientService.removeChild(clientLogin, specialistLogin);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
