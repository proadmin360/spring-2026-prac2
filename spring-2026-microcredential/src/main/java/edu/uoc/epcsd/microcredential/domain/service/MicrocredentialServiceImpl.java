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

@RequiredArgsConstructor
@Service
@Validated
public class MicrocredentialServiceImpl implements MicrocredentialService {

    private final MicrocredentialRepository microcredentialRepository;
	private final KafkaTemplate<String, MicrocredentialMessage> microcredentialKafkaTemplate;

    @Value("${courseService.url}")
    private String courseServiceUrl;
    
	@Override
	public Optional<Microcredential> getMicrocredentialById(@NotNull Long microcredentialId) {
		return microcredentialRepository.getMicrocredentialById(microcredentialId);
	}

	@Override
	public List<Microcredential> getPendingMicrocredentialRequests() {
		return microcredentialRepository.findByStatus(MicrocredentialStatus.REQUESTED);
	}
	
	//TODO: approvePendingMicrocredential
    

    //TODO: rejectPendingMicrocredential() 


	//TODO: requestCourseMicrocredentials()
	

	
	//TODO: getPendingMicrocredentialRequests() 
    
}
