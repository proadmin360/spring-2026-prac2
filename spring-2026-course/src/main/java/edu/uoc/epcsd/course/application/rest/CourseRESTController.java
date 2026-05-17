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

    /**
     * Consultem un curs pel seu identificador on es retorna el curs 
     * si existeix o una resposta 404 si no es troba cap curs amb l’identificador indicat.
     */
    @GetMapping("/{courseId}")
    public ResponseEntity<Course> getCourseById(@PathVariable @NotNull Long courseId) {
        log.trace("getCourseById");

        return courseService.getCourseById(courseId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Recuperem totes les inscripcions associades a un curs concret. Aquest endpoint 
     * permet obtenir la llista d’alumnes matriculats en un curs determinat a partir del seu identificador.
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
     * Recuperem la llista de cursos que estan disponibles.
     */
    @GetMapping
    public ResponseEntity<List<Course>> findCourses() {
        return ResponseEntity.ok(courseService.findCourses());
    }
    
    /**
     * Creem un nou curs al sistema.
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
     * Modifiquem les dades principals d’un curs existent.
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
     * Obrim el període d’inscripció d’un curs.
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
     * Consulta els estudiants inscrits en un curs.
     */
    @GetMapping("/{courseId}/students")
    public ResponseEntity<List<Enrollment>> getEnrolledStudents(@PathVariable Long courseId) {

        List<Enrollment> enrollments = courseService.getEnrolledStudents(courseId);

        return ResponseEntity.ok(enrollments);
    }

    /**
     * Es tanca el període d’inscripció d’un curs.
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
     * Es tanca un curs existent.
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
    
    /**
     * Es tanca l'acta de qualificacions d’un curs.
     */
    @PatchMapping("/{courseId}/close-grade-reports")
    public ResponseEntity<Boolean> closeGradeReports(@PathVariable Long courseId) {

        Boolean result = courseService.closeGradeReports(courseId);

        if (!result) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(true);
    }

    @PostMapping("/{courseId}/enroll")
    public ResponseEntity<Boolean> enrollInCourse(
            @PathVariable Long courseId,
            @RequestParam String userEmail) {

        Boolean result = courseService.enrollInCourse(courseId, userEmail);

        if (!result) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(true);
    }
    
 
}
