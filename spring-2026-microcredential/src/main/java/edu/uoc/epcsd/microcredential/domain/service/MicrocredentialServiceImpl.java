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
	 * Consulta les microcredencials pendents de validació.
	 *
	 * El mètode recupera del repositori totes les microcredencials
	 * que es troben en estat REQUESTED.
	 *
	 * Desenvolupat per Jaume Jurado.
	 */
	@Override
	public List<Microcredential> getPendingMicrocredentialRequests() {
		return microcredentialRepository.findByStatus(MicrocredentialStatus.REQUESTED);
	}
	
	/**
	 * Aprova una microcredencial pendent.
	 *
	 * El mètode valida que la microcredencial existeixi i que es trobi
	 * en estat REQUESTED. Si la validació és correcta, l’estat passa
	 * a GRANTED, s’actualitza la data d’assignació i es publica
	 * una notificació asíncrona mitjançant Kafka.
	 *
	 * Desenvolupat per Jaume Jurado.
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
	 * Gestiona el rebuig d’una microcredencial pendent de validació.
	 *
	 * El mètode comprova que la microcredencial existeixi i que es trobi
	 * en estat REQUESTED abans d’actualitzar-ne l’estat a REJECTED
	 * i desar els canvis al sistema.
	 *
	 * Desenvolupat per Jaume Jurado.
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

		microcredentialRepository.save(microcredential);

		return true;
	}


	/**
	 * Sol·licita la generació de microcredencials per a totes les inscripcions d’un curs.
	 *
	 * El mètode consulta el microservei de cursos per obtenir les inscripcions associades
	 * al curs indicat, crea una microcredencial pendent per a cada inscripció i publica
	 * una notificació asíncrona perquè els administradors en siguin informats.
	 *
	 * Desenvolupat per Jaume Jurado.
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
