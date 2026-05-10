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
     * Recupera totes les microcredencials pendents de validació.
     *
     * Aquest endpoint permet a l’administrador consultar les peticions
     * de microcredencial que encara es troben en estat REQUESTED.
     *
     * Desenvolupat per Jaume Jurado.
     */
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingMicrocredentialRequests() {
        return ResponseEntity.ok(
                microcredentialService.getPendingMicrocredentialRequests()
        );
    }


    /**
     * Aprova una microcredencial pendent.
     *
     * Aquest endpoint permet validar una petició existent i canviar-ne
     * l’estat a GRANTED quan encara es troba pendent de revisió.
     *
     * Desenvolupat per Jaume Jurado.
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
     * Rebutja una microcredencial pendent.
     *
     * Aquest endpoint permet marcar una petició existent com a REJECTED
     * sempre que encara es trobi pendent de validació.
     *
     * Desenvolupat per Jaume Jurado.
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
     *
     * Desenvolupat per Jaume Jurado.
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
