package edu.uoc.epcsd.course.domain.service;

import edu.uoc.epcsd.course.application.rest.response.GetUserResponse;
import edu.uoc.epcsd.course.domain.Course;
import edu.uoc.epcsd.course.domain.Enrollment;

import java.util.Date;

import java.util.List;
import java.util.Optional;

public interface CourseService {
    
	Optional<Course> getCourseById(Long courseId);
	
	List<Course> findCourses();

    Course createCourse(Course course);

    Course modifyCourseDetails(Long courseId, Course course);

    Boolean closeGradeReports(Long courseId);

    Boolean openEnrollment(Long courseId);

    Boolean enrollInCourse(Long courseId, String userEmail);

    Boolean closeEnrollment(Long courseId);

    List<Enrollment> getEnrolledStudents(Long courseId);

    Boolean closeCourse(Long courseId);

}
