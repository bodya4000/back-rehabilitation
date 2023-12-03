package veres.lection.first.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import veres.lection.first.rest.business.ReHubService;
import veres.lection.first.rest.business.SpecialistService;
import veres.lection.first.rest.exception.NotFoundIdException;
import veres.lection.first.rest.model.ReHubModel;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rehub-section")
public class ReHubController{

    @Autowired
    private ReHubService reHubService;

    @Autowired
    private SpecialistService specialistService;

    @GetMapping
    public List<ReHubModel> getAllReHubs(){
        return reHubService.getAll();
    }

    @GetMapping("{id}")
    public ReHubModel getById(@PathVariable("id") int id) throws NotFoundIdException {
        return reHubService.getById(id);
    }

    @PostMapping
    public ResponseEntity<Integer> create(@RequestBody ReHubModel reHubModel) throws NotFoundIdException {
        reHubService.persist(reHubModel);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @PatchMapping("{id}")
    public ReHubModel update(
            @PathVariable("id") int id,
            @RequestBody Map<String, Object> updates
            ) throws NotFoundIdException {
        reHubService.update(id, updates);
        return reHubService.getById(id);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Integer> delete(@PathVariable("id") int id) throws NotFoundIdException {
        reHubService.delete(reHubService.getById(id));
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PostMapping("/{rehubId}/specialist/{specialistId}")
    public ResponseEntity<Integer> addNewSpecialist(
            @PathVariable int specialistId,
            @PathVariable int rehubId
    ) throws NotFoundIdException {
        var specialist = specialistService.getById(specialistId);
        var reHub = reHubService.getById(rehubId);
        reHubService.addSpecialistById(reHub, specialist);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{rehubId}/specialist/{specialistId}")
    public ResponseEntity<Integer> removeNewSpecialist(
            @PathVariable("rehubId") int rehubId,
            @PathVariable("specialistId") int specialistId) throws NotFoundIdException {
        var specialist = specialistService.getById(specialistId);
        var reHub = reHubService.getById(rehubId);
        reHubService.removeSpecialistById(reHub, specialist);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
