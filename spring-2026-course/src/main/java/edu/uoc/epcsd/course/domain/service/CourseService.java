package edu.uoc.epcsd.course.domain.service;

import edu.uoc.epcsd.course.application.rest.response.GetUserResponse;
import edu.uoc.epcsd.course.domain.Course;

import java.util.Date;

import java.util.List;
import java.util.Optional;

public interface CourseService {
    
	Optional<Course> getCourseById(Long courseId);
	
	List<Course> findCourses();

    Course createCourse(Course course);

    Course modifyCourseDetails(Long courseId, Course course);

    Boolean openEnrollment(Long courseId);

    Boolean closeEnrollment(Long courseId);

    Boolean closeCourse(Long courseId);

}
