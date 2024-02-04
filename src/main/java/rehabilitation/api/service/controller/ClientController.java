package rehabilitation.api.service.controller;

import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rehabilitation.api.service.dto.entities.ClientDto;
import rehabilitation.api.service.business.businessServices.clientBusiness.ClientService;
import rehabilitation.api.service.exceptionHandling.exception.buisness.NotFoundLoginException;

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

    @GetMapping("/client")
    @RolesAllowed("ADMIN")
    public List<ClientDto> getClients() {
        return clientService.getAllModelView();
    }


    @GetMapping("/client/{login}")
    @PreAuthorize("isAuthenticated()")
    public ClientDto getClientById(@PathVariable("login") String login) throws NotFoundLoginException {
        return clientService.getModelDtoByLogin(login);
    }


    @PatchMapping("/client/{login}")
    @PreAuthorize("#login == authentication.principal")
    public ResponseEntity<String> updateClient(@PathVariable("login") String login, @RequestBody Map<String, Object> updates) throws NotFoundLoginException {
        clientService.updateModel(login, updates);
        return ResponseEntity.status(HttpStatus.OK).body("client updated");
    }

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
        clientService.addSpecialist(clientLogin, specialistLogin);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @DeleteMapping("/{clientLogin}/specialist/{specialistLogin}")
    @PreAuthorize("#clientLogin == authentication.principal.username")
    public ResponseEntity<Integer> removeSpecialist(@PathVariable("clientLogin") String clientLogin, @PathVariable("specialistLogin") String specialistLogin) throws NotFoundLoginException {
        clientService.removeSpecialist(clientLogin, specialistLogin);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
