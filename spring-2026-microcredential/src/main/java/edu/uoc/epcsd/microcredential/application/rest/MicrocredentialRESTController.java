package edu.uoc.epcsd.microcredential.application.rest;


import edu.uoc.epcsd.microcredential.domain.Microcredential;
import edu.uoc.epcsd.microcredential.domain.service.MicrocredentialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.constraints.NotNull;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/microcredentials")
public class MicrocredentialRESTController {

    private final MicrocredentialService microcredentialService;

    @GetMapping("/{microcredentialId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Microcredential> getMicrocredentialById(@PathVariable @NotNull Long microcredentialId) {
        return microcredentialService.getMicrocredentialById(microcredentialId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    

    /**
     * Recuperem totes les credencials que estan pendents de validació.
     * Podem consultar les peticions de micro. en estat REQUESTED
     */
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingMicrocredentialRequests() {
        return ResponseEntity.ok(
                microcredentialService.getPendingMicrocredentialRequests()
        );
    }


    /**
     * Aprovem una micro. que está en pendent i canviar a grantend
     * quan encara está pendent de revisió
     */
    @PatchMapping("/{microcredentialId}/approve")
    public ResponseEntity<Boolean> approvePendingMicrocredential(
            @PathVariable @NotNull Long microcredentialId) {

        Boolean result = microcredentialService.approvePendingMicrocredential(microcredentialId);

        if (!result) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(true);
    }

    /**
     * Rebutjem una microcredencial pendent. 
     * 
     * Aquest endpoint ens permet marcar una petició existent 
     * com a REJECTED sempre i quan, que encara es trobi pendent de validació.
     */
    @PatchMapping("/{microcredentialId}/reject")
    public ResponseEntity<Boolean> rejectPendingMicrocredential(
            @PathVariable @NotNull Long microcredentialId) {

        Boolean result = microcredentialService.rejectPendingMicrocredential(microcredentialId);

        if (!result) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(true);
    }

    /**
     * Sol·licita microcredencials per a un curs completat. 
     * 
     * Aquest endpoint inicia la creació de peticions de microcredencial 
     * a partir de les inscripcions associades al curs indicat.
     */
    @PostMapping("/courses/{courseId}/request")
    public ResponseEntity<Boolean> requestCourseMicrocredentials(
            @PathVariable @NotNull Long courseId) {

        Boolean result = microcredentialService.requestCourseMicrocredentials(courseId);

        if (!result) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(true);
    }
}
