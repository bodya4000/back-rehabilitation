package rehabilitation.api.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rehabilitation.api.service.dto.RegistrationDto;
import rehabilitation.api.service.dto.RehubDto;
import rehabilitation.api.service.entity.ReHubModel;
import rehabilitation.api.service.business.ReHubService;
import rehabilitation.api.service.exceptionHandling.exception.AlreadyExistLoginException;
import rehabilitation.api.service.exceptionHandling.exception.NotFoundLoginException;
import rehabilitation.api.service.exceptionHandling.exception.NullLoginException;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/rehub-section")
public class ReHubController{


    @Autowired
    private ReHubService reHubService;

    @GetMapping("/rehub")
    public List<RehubDto> getAllReHubs(){
        return reHubService.getAllModelView();
    }

    @GetMapping("/rehub/{login}")
    public RehubDto getByLogin(@PathVariable("login") String login) throws NotFoundLoginException {
        return reHubService.getModelViewByLogin(login);
    }

    @PostMapping
    public ResponseEntity<Integer> create(@RequestBody RegistrationDto registrationDto
    ) throws AlreadyExistLoginException, NullLoginException {
        reHubService.signUpModel(registrationDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @PatchMapping("{login}")
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
    public ResponseEntity<Integer> delete(@PathVariable("login") String login) throws NotFoundLoginException {
        reHubService.deleteModel(login);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PostMapping("/{rehubLogin}/specialist/{specialistLogin}")
    public ResponseEntity<Integer> addNewSpecialist(
            @PathVariable String rehubLogin,
            @PathVariable String specialistLogin
    ) throws NotFoundLoginException {
        reHubService.addChild(rehubLogin, specialistLogin);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{rehubLogin}/specialist/{specialistLogin}")
    public ResponseEntity<Integer> removeNewSpecialist(
            @PathVariable("rehubLogin") String rehubLogin,
            @PathVariable("specialistLogin") String specialistLogin) throws NotFoundLoginException {
        reHubService.removeChild(rehubLogin, specialistLogin);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
