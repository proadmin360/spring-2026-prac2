package edu.uoc.epcsd.microcredential.application.rest.response;

import lombok.*;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponse {

    private Long id;
    private String student;
    private Long qualification;
    private String status;
    private Long courseId;
}