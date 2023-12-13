package rehabilitation.api.service.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rehabilitation.api.service.business.SpecialistService;
import rehabilitation.api.service.entity.ReHubModel;
import rehabilitation.api.service.business.ReHubService;
import rehabilitation.api.service.exception.NotFoundIdException;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/rehub-section")
public class ReHubController{


    @Autowired
    private ReHubService reHubService;

    @GetMapping("/rehub")
    public List<ReHubModel> getAllReHubs(){
        return reHubService.getAll();
    }

    @GetMapping("/rehub/{login}")
    public ReHubModel getById(@PathVariable("login") String login) throws NotFoundIdException {
        return reHubService.getById(login);
    }

    @PostMapping
    public ResponseEntity<Integer> create(@RequestBody ReHubModel reHubModel
    ) throws NotFoundIdException {
        reHubService.save(reHubModel);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @PatchMapping("{login}")
    public ResponseEntity<String> update(
            @PathVariable("login") String login,
            @RequestBody Map<String, Object> updates
            ) throws NotFoundIdException {
        reHubService.updateRehub(login, updates);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("rehub updated");
    }

    @DeleteMapping("/rehub/{login}")
    public ResponseEntity<Integer> delete(@PathVariable("login") String login) throws NotFoundIdException {
        reHubService.delete(reHubService.getById(login));
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PostMapping("/{rehubId}/specialist/{specialistLogin}")
    public ResponseEntity<Integer> addNewSpecialist(
            @PathVariable String specialistLogin,
            @PathVariable String rehubId
    ) throws NotFoundIdException {
        reHubService.addSpecialistById(rehubId, specialistLogin);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{rehubId}/specialist/{specialistLogin}")
    public ResponseEntity<Integer> removeNewSpecialist(
            @PathVariable("rehubId") String rehubId,
            @PathVariable("specialistLogin") String specialistLogin) throws NotFoundIdException {
        reHubService.removeSpecialist(rehubId, specialistLogin);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }



}
