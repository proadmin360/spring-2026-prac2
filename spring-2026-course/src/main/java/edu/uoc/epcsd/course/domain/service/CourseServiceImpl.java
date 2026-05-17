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
        
    /**
     * Inscriu un usuari en un curs si el cusrs
     * existeix i crea/desa la matrícula
     */
    @Override
    public Boolean enrollInCourse(Long courseId, String userEmail) {

        Optional<Course> optionalCourse = courseRepository.getCourseById(courseId);

        if (optionalCourse.isEmpty()) {
            return false;
        }

        Enrollment enrollment = Enrollment.builder()
                .student(userEmail)
                .courseId(courseId)
                .enrollmentDate(new Date())
                .qualification(0L)
                .build();

        enrollmentRepository.createEnrollment(enrollment);

        return true;
    }
    
    /**
     * Recupera els estudiants qeu s'han inscrit a un curs.
     * Retonrmen totes les matrícules associades al curs
     */
    @Override
    public List<Enrollment> getEnrolledStudents(Long courseId) {
        return enrollmentRepository.findEnrollmentByCourse(courseId);
    }

    // TODO enrollInCourse()

    /**
     * Tanqume l'acta de qualificacions d’un curs i     *
     * actualització de l’estat del curs a PENDING_CLOSUE.
     */
    @Override
    public Boolean closeGradeReports(Long courseId) {
        return updateCourseStatus(courseId, CourseStatus.PENDING_CLOSUE);
    }

    
    /**
     * Retorna la llista de cursos disponibles al sistema.
     */
    @Override
    public List<Course> findCourses() {
        return courseRepository.findCourses();
    }

    /**
     * En permet modifiacar les dades d’un curs existent.
     *
     * TAmbé comproven que el curs existeixi abans de desar-ne les noves dades (!important).
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
     * Creem un nou curs al sistema desant el curs al repositori
     */
    @Override
    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    /**
     * S'Obre el període d’inscripció d’un curs existent on es comprova
     * que existeixi el curs i actualizta el seu estat a ENROLLMENT_OPEN
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
     * Tanquem el període d'inscripció, comprovem que el curs
     * existeix y canviem l'entat a ACTIVE
     *
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
     * Es tanca un curs que existeix y canviem l'etat a CLOSED.
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


    /**
     * falta comentar mètode
     * 
     */
    private Boolean updateCourseStatus(Long courseId, CourseStatus newStatus) {

        Optional<Course> optionalCourse = courseRepository.getCourseById(courseId);

        if (optionalCourse.isEmpty()) {
            return false;
        }

        Course course = optionalCourse.get();
        course.setStatus(newStatus);

        courseRepository.save(course);

        return true;
    }
}
