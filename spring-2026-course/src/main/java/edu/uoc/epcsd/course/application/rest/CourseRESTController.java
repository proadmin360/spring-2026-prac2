package edu.uoc.epcsd.course.application.rest;

import edu.uoc.epcsd.course.application.rest.request.CourseRequest;
import edu.uoc.epcsd.course.application.rest.response.GetUserResponse;
import edu.uoc.epcsd.course.domain.Course;
import edu.uoc.epcsd.course.domain.Enrollment;
import edu.uoc.epcsd.course.domain.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
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

    // TODO findCourses()
    
    // TODO getEnrolledStudents()
    // TODO createCourse()
    // TODO modifyCourseDetails()
    // TODO openEnrollment()
    // TODO closeEnrollment()
    // TODO enrollInCourse()
    // TODO closeGradeReports()
    // TODO closeCourse()
    
 
}
