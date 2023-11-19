package veres.lection.first.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import veres.lection.first.rest.business.ClientService;
import veres.lection.first.rest.exception.NotFoundIdException;
import veres.lection.first.rest.model.ClientModel;

import java.util.*;

@RestController
@RequestMapping("/client-section")
public class ClientController {
    
    @Autowired
    ClientService clientService;

    /*
    * This method returns all clients from database
    * */
    @GetMapping("/client")
    public List<ClientModel> getClients() {
        return clientService.getClientsList();
    }

    /*
     * This method returns a client by id from database
     * */
    @GetMapping("/client/{id}")
    public ClientModel getClientById(
            @PathVariable("id") int id
    ) throws NotFoundIdException {
        return clientService.getClientById(id);
    }

    /*
     * This method create a client in database and returns its json
     * */
    @PostMapping(value = "/client")
    public ResponseEntity<ClientModel> createClient(@RequestBody ClientModel clientModel) {
        clientService.saveClient(clientModel);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(clientModel);
    }

    /*
     * This method updates a client by id in database and returns its json
     * */
    @PatchMapping("/client/{id}")
    public ClientModel updateClient(@PathVariable("id") int id,
                                    @RequestBody Map<String, Object> updates) throws NotFoundIdException {
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

}
