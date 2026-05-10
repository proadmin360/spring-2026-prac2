package edu.uoc.epcsd.course.application.rest;

import edu.uoc.epcsd.course.application.rest.request.CourseRequest;
import edu.uoc.epcsd.course.domain.Course;
import edu.uoc.epcsd.course.domain.Enrollment;
import edu.uoc.epcsd.course.domain.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

import edu.uoc.epcsd.course.domain.service.EnrollmentService;


@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/courses")
public class CourseRESTController {

    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
   
    @GetMapping("/{courseId}")
    public ResponseEntity<Course> getCourseById(@PathVariable @NotNull Long courseId) {
        log.trace("getCourseById");

        return courseService.getCourseById(courseId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Recupera totes les inscripcions associades a un curs concret.
     *
     * Aquest endpoint permet obtenir la llista d’alumnes matriculats
     * en un curs determinat a partir del seu identificador.
     *
     * Desenvolupat per Jaume Jurado.
     */
    @GetMapping("/{courseId}/enrollments")
    public ResponseEntity<List<Enrollment>> getEnrollmentsByCourse(
            @PathVariable @NotNull Long courseId) {

        log.trace("getEnrollmentsByCourse");

        return ResponseEntity.ok(
                enrollmentService.getEnrollmentsByCourse(courseId)
        );
    }

    /**
     * Recupera la llista de cursos disponibles.
     *
     * Desenvolupat per Jaume Jurado.
     */
    @GetMapping
    public ResponseEntity<List<Course>> findCourses() {
        return ResponseEntity.ok(courseService.findCourses());
    }
    
    /**
     * Crea un nou curs al sistema.
     *
     * Desenvolupat per Jaume Jurado.
     */
    @PostMapping
    public ResponseEntity<Course> createCourse(@Valid @RequestBody CourseRequest request) {

        Course course = Course.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .instructor(request.getInstructor())
                .enrollmentStartDate(request.getEnrollmentStartDate())
                .enrollmentEndDate(request.getEnrollmentEndDate())
                .mode(request.getMode())
                .price(request.getPrice())
                .objectives(request.getObjectives())
                .methology(request.getMethology())
                .duration(request.getDuration())
                .language(request.getLanguage())
                .location(request.getLocation())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(courseService.createCourse(course));
    }
    
    /**
     * Modifica les dades principals d’un curs existent.
     *
     * Desenvolupat per Jaume Jurado.
     */
    @PutMapping("/{courseId}")
    public ResponseEntity<Course> modifyCourseDetails(
            @PathVariable @NotNull Long courseId,
            @RequestBody Course course) {

        Course updatedCourse = courseService.modifyCourseDetails(courseId, course);

        if (updatedCourse == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updatedCourse);
    }

    /**
     * Obre el període d’inscripció d’un curs.
     *
     * Desenvolupat per Jaume Jurado.
     */
    @PatchMapping("/{courseId}/open-enrollment")
    public ResponseEntity<Boolean> openEnrollment(@PathVariable @NotNull Long courseId) {
        Boolean result = courseService.openEnrollment(courseId);

        if (!result) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(true);
    }

    /**
     * Tanca el període d’inscripció d’un curs.
     *
     * Desenvolupat per Jaume Jurado.
     */
    @PatchMapping("/{courseId}/close-enrollment")
    public ResponseEntity<Boolean> closeEnrollment(@PathVariable @NotNull Long courseId) {
        Boolean result = courseService.closeEnrollment(courseId);

        if (!result) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(true);
    }
    

    /**
     * Tanca un curs existent.
     *
     * Desenvolupat per Jaume Jurado.
     */
    @PatchMapping("/{courseId}/close")
    public ResponseEntity<Boolean> closeCourse(@PathVariable @NotNull Long courseId) {
        Boolean result = courseService.closeCourse(courseId);

        if (!result) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(true);
    }

    
    // TODO getEnrolledStudents()    
    // TODO enrollInCourse()
    // TODO closeGradeReports()
    
    
 
}
