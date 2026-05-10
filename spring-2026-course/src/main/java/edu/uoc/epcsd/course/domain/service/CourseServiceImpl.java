package edu.uoc.epcsd.course.domain.service;

import edu.uoc.epcsd.course.application.rest.response.GetUserResponse;
import edu.uoc.epcsd.course.domain.Course;
import edu.uoc.epcsd.course.domain.CourseStatus;
import edu.uoc.epcsd.course.domain.Enrollment;
import edu.uoc.epcsd.course.domain.repository.CourseRepository;
import edu.uoc.epcsd.course.domain.repository.EnrollmentRepository;
import edu.uoc.epcsd.course.domain.repository.UserRepository;
import edu.uoc.epcsd.course.infrastructure.repository.jpa.EnrollmentEntity;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final EnrollmentService enrollmentService;
    
    @Value("${userService.getUserByEmail.url}")
    private String usersServiceUrl;

    @Value("${credentialService.create.url}")
    private String microcredentialServiceUrl;
    
    
    @Override
    public Optional<Course> getCourseById(Long courseId) {
        return courseRepository.getCourseById(courseId);
    }
        
    // TODO getEnrollmentsByCourse()
    // TODO getEnrolledStudents() 
    // TODO enrollInCourse()
    // TODO closeGradeReports()

    
    /**
     * Retorna la llista de cursos disponibles al sistema.
     *
     * Desenvolupat per Jaume Jurado.
     */
    @Override
    public List<Course> findCourses() {
        return courseRepository.findCourses();
    }

    /**
     * Modifica les dades d’un curs existent.
     *
     * El mètode comprova que el curs existeixi abans de desar-ne
     * les noves dades.
     *
     * Desenvolupat per Jaume Jurado.
     */
    @Override
    public Course modifyCourseDetails(Long courseId, Course course) {

        Optional<Course> optionalCourse = courseRepository.getCourseById(courseId);

        if (optionalCourse.isEmpty()) {
            return null;
        }

        course.setId(courseId);

        return courseRepository.save(course);
    }

    /**
     * Crea un nou curs al sistema.
     *
     * El mètode desa el curs rebut al repositori.
     *
     * Desenvolupat per Jaume Jurado.
     */
    @Override
    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    /**
     * Obre el període d’inscripció d’un curs existent.
     *
     * El mètode comprova que el curs existeixi i actualitza
     * el seu estat a ENROLLMENT_OPEN.
     *
     * Desenvolupat per Jaume Jurado.
     */
    @Override
    public Boolean openEnrollment(Long courseId) {

        Optional<Course> optionalCourse = courseRepository.getCourseById(courseId);

        if (optionalCourse.isEmpty()) {
            return false;
        }

        Course course = optionalCourse.get();
        course.setStatus(CourseStatus.ENROLLMENT_OPEN);

        courseRepository.save(course);

        return true;
    }

    /**
     * Tanca el període d’inscripció d’un curs existent.
     *
     * El mètode comprova que el curs existeixi i actualitza
     * el seu estat a ACTIVE.
     *
     * Desenvolupat per Jaume Jurado.
     */
    @Override
    public Boolean closeEnrollment(Long courseId) {

        Optional<Course> optionalCourse = courseRepository.getCourseById(courseId);

        if (optionalCourse.isEmpty()) {
            return false;
        }

        Course course = optionalCourse.get();
        course.setStatus(CourseStatus.ACTIVE);

        courseRepository.save(course);

        return true;
    }
    
    /**
     * Tanca un curs existent.
     *
     * El mètode comprova que el curs existeixi i, en cas afirmatiu,
     * actualitza el seu estat a CLOSED i desa els canvis.
     *
     * Desenvolupat per Jaume Jurado.
     */
    @Override
    public Boolean closeCourse(Long courseId) {

        Optional<Course> optionalCourse = courseRepository.getCourseById(courseId);

        if (optionalCourse.isEmpty()) {
            return false;
        }

        Course course = optionalCourse.get();
        course.setStatus(CourseStatus.CLOSED);

        courseRepository.save(course);

        return true;
    }
}
