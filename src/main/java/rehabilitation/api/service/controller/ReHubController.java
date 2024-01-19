package rehabilitation.api.service.controller;

import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rehabilitation.api.service.dto.RehubDto;
import rehabilitation.api.service.business.businessServices.reHubBusiness.ReHubService;
import rehabilitation.api.service.exceptionHandling.exception.NotFoundLoginException;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/rehub-section")
public class ReHubController{


    @Autowired
    private ReHubService reHubService;

    @GetMapping("/rehub")
    @RolesAllowed("ADMIN")
    public List<RehubDto> getAllReHubs(){
        return reHubService.getAllModelView();
    }

    @GetMapping("/rehub/{login}")
    public RehubDto getByLogin(@PathVariable("login") String login) throws NotFoundLoginException {
        return reHubService.getModelDtoByLogin(login);
    }

    @PatchMapping("{login}")
    @PreAuthorize("#login == authentication.principal")
    public ResponseEntity<String> update(
            @PathVariable("login") String login,
            @RequestBody Map<String, Object> updates
            ) throws NotFoundLoginException {
        reHubService.updateModel(login, updates);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("rehub updated");
    }

    @DeleteMapping("/rehub/{login}")
    @RolesAllowed("ADMIN")
    public ResponseEntity<Integer> delete(@PathVariable("login") String login) throws NotFoundLoginException {
        reHubService.deleteModel(login);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PostMapping("/{rehubLogin}/specialist/{specialistLogin}")
    @PreAuthorize("#rehubLogin == authentication.principal")
    public ResponseEntity<Integer> addNewSpecialist(
            @PathVariable String rehubLogin,
            @PathVariable String specialistLogin
    ) throws NotFoundLoginException {
        reHubService.addChild(rehubLogin, specialistLogin);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{rehubLogin}/specialist/{specialistLogin}")
    @PreAuthorize("#rehubLogin == authentication.principal")
    public ResponseEntity<Integer> removeNewSpecialist(
            @PathVariable("rehubLogin") String rehubLogin,
            @PathVariable("specialistLogin") String specialistLogin) throws NotFoundLoginException {
        reHubService.removeChild(rehubLogin, specialistLogin);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
