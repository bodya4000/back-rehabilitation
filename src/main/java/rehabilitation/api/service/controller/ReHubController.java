package rehabilitation.api.service.controller;

import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rehabilitation.api.service.dto.entities.RehubDto;
import rehabilitation.api.service.business.businessServices.reHubBusiness.ReHubService;
import rehabilitation.api.service.exceptionHandling.exception.buisness.NotFoundLoginException;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/rehub-section")
@RequiredArgsConstructor
public class ReHubController{

    private final ReHubService reHubService;

    @GetMapping("/rehub")
    @RolesAllowed("ADMIN")
    public ResponseEntity<List<RehubDto>> getAllReHubs(){
        return ResponseEntity.ok(reHubService.getAllModelView());
    }

    @GetMapping("/rehub/{login}")
    public ResponseEntity<RehubDto> getByLogin(@PathVariable("login") String login) throws NotFoundLoginException {
        return ResponseEntity.ok(reHubService.getModelDtoByLogin(login));
    }

    @PatchMapping("/rehub/{login}")
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
    public ResponseEntity<String> delete(@PathVariable("login") String login) throws NotFoundLoginException {
        reHubService.deleteModel(login);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("rehub deleted");
    }

    @PostMapping("/{rehubLogin}/specialist/{specialistLogin}")
    @PreAuthorize("#rehubLogin == authentication.principal")
    public ResponseEntity<Integer> addNewSpecialist(
            @PathVariable String rehubLogin,
            @PathVariable String specialistLogin
    ) throws NotFoundLoginException {
        reHubService.addSpecialist(rehubLogin, specialistLogin);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{rehubLogin}/specialist/{specialistLogin}")
    @PreAuthorize("#rehubLogin == authentication.principal")
    public ResponseEntity<Integer> removeNewSpecialist(
            @PathVariable("rehubLogin") String rehubLogin,
            @PathVariable("specialistLogin") String specialistLogin) throws NotFoundLoginException {
        reHubService.removeSpecialist(rehubLogin, specialistLogin);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
