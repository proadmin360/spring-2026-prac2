package edu.uoc.epcsd.microcredential.infrastructure.repository.jpa;

import edu.uoc.epcsd.microcredential.domain.Microcredential;
import edu.uoc.epcsd.microcredential.domain.repository.MicrocredentialRepository;
import lombok.RequiredArgsConstructor;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MicrocredentialRepositoryImpl implements MicrocredentialRepository {

    private final SpringDataMicrocredentialRepository jpaMicrocredentialRepository;
    
    @Override
    public Optional<Microcredential> getMicrocredentialById(Long microcredentialId) {
        return jpaMicrocredentialRepository.findById(microcredentialId)
                .map(MicrocredentialEntity::toDomain);
    } 

    //TODO: createMicrocredential() 
    
    //TODO: updateStatusPendingMicrocredential()
    
    //TODO: getPendingMicrocredentialRequests()

    
}
