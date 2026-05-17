package edu.uoc.epcsd.microcredential.domain.service;

import edu.uoc.epcsd.microcredential.domain.Microcredential;
import edu.uoc.epcsd.microcredential.domain.repository.MicrocredentialRepository;
import edu.uoc.epcsd.microcredential.infrastructure.kafka.MicrocredentialMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import edu.uoc.epcsd.microcredential.domain.MicrocredentialStatus;
import java.util.List;
import org.springframework.web.client.RestTemplate;
import edu.uoc.epcsd.microcredential.application.rest.response.EnrollmentResponse;
import java.util.Arrays;
import java.util.Date;

@RequiredArgsConstructor
@Service
@Validated
public class MicrocredentialServiceImpl implements MicrocredentialService {

    private final MicrocredentialRepository microcredentialRepository;
	private final KafkaTemplate<String, MicrocredentialMessage> microcredentialKafkaTemplate;
	private final RestTemplate restTemplate;

    @Value("${courseService.url}")
    private String courseServiceUrl;
    
	@Override
	public Optional<Microcredential> getMicrocredentialById(@NotNull Long microcredentialId) {
		return microcredentialRepository.getMicrocredentialById(microcredentialId);
	}

	/**
	 * Consulta les micro. que tenim pendents de validació.
	 */
	@Override
	public List<Microcredential> getPendingMicrocredentialRequests() {
		return microcredentialRepository.findByStatus(MicrocredentialStatus.REQUESTED);
	}
	
	/**
	 * Aprovem una micro. pendent sempre que la mirco. 
	 * existeixi en estat REQUESTED, canviem estat a a GRANTED. Actualitzem
	 * data assignació y es publica una notificació asíncrona a través de KAFKA.
	 */
	@Override
	public Boolean approvePendingMicrocredential(@NotNull Long microcredentialId) {

		Optional<Microcredential> optionalMicrocredential =
				microcredentialRepository.getMicrocredentialById(microcredentialId);

		if (optionalMicrocredential.isEmpty()) {
			return false;
		}

		Microcredential microcredential = optionalMicrocredential.get();

		if (microcredential.getStatus() != MicrocredentialStatus.REQUESTED) {
			return false;
		}

		microcredential.setStatus(MicrocredentialStatus.GRANTED);
		microcredential.setAssignmentDate(new Date());

		Microcredential savedMicrocredential =
				microcredentialRepository.save(microcredential);

		MicrocredentialMessage message = MicrocredentialMessage.builder()
				.microcredentialId(savedMicrocredential.getId())
				.enrollment(savedMicrocredential.getEnrollment())
				.build();

		microcredentialKafkaTemplate.send("microcredential.microcredential_approved", message);

		return true;
	}

	
	/**
	 * Rebutjem la micro. que está pendent de validació sempre
	 * que existeixi en estat REQUESTED. Canviem a esta REJECTED
	 */
	@Override
	public Boolean rejectPendingMicrocredential(@NotNull Long microcredentialId) {

		Optional<Microcredential> optionalMicrocredential =
				microcredentialRepository.getMicrocredentialById(microcredentialId);

		if (optionalMicrocredential.isEmpty()) {
			return false;
		}

		Microcredential microcredential = optionalMicrocredential.get();

		if (microcredential.getStatus() != MicrocredentialStatus.REQUESTED) {
			return false;
		}

		microcredential.setStatus(MicrocredentialStatus.REJECTED);

		Microcredential savedMicrocredential =
				microcredentialRepository.save(microcredential);

		MicrocredentialMessage message = MicrocredentialMessage.builder()
				.microcredentialId(savedMicrocredential.getId())
				.enrollment(savedMicrocredential.getEnrollment())
				.build();

		microcredentialKafkaTemplate.send("microcredential.microcredential_rejected", message);

		return true;
	}


	/**
	 * Sol·licita la generació de microcredencials per a totes les inscripcions d’un curs.
	 * Consultem el microservei de cursos per obtenir les inscripcions associades
	 * al curs indicat i es crea una microcredencial pendent per a cada inscripció. Publiquem
	 * una notificació asíncrona perquè els administradors en siguin informats.
	 */
	@Override
	public Boolean requestCourseMicrocredentials(@NotNull Long courseId) {

		String url = courseServiceUrl + "/" + courseId + "/enrollments";

		EnrollmentResponse[] enrollments =
				restTemplate.getForObject(url, EnrollmentResponse[].class);

		if (enrollments == null || enrollments.length == 0) {
			return false;
		}

		Arrays.stream(enrollments)
				.forEach(enrollment -> {

					Microcredential microcredential = Microcredential.builder()
							.submitDate(new Date())
							.status(MicrocredentialStatus.REQUESTED)
							.content("Microcredential requested for enrollment " + enrollment.getId())
							.enrollment(enrollment.getId())
							.assignmentDate(new Date())
							.build();

					Microcredential savedMicrocredential =
							microcredentialRepository.save(microcredential);

					MicrocredentialMessage message = MicrocredentialMessage.builder()
							.microcredentialId(savedMicrocredential.getId())
							.userEmail(enrollment.getStudent())
							.courseId(courseId)
							.enrollment(enrollment.getId())
							.build();

					microcredentialKafkaTemplate.send("microcredential.microcredential_pending", message);
				});

		return true;
	}
}
