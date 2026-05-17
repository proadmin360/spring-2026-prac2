package edu.uoc.epcsd.microcredential.infrastructure.repository.jpa;

import edu.uoc.epcsd.microcredential.domain.Microcredential;
import edu.uoc.epcsd.microcredential.domain.repository.MicrocredentialRepository;
import lombok.RequiredArgsConstructor;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

import edu.uoc.epcsd.microcredential.domain.MicrocredentialStatus;
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


    /**
     * Es recupera les microcredencials filtrades pel seu estat.
     * 
     * Consultem el repositori JPA i transformem les entitats
     * persistents en objectes del domini.
     */

    @Override
    public List<Microcredential> findByStatus(MicrocredentialStatus status) {

        return jpaMicrocredentialRepository.findByStatus(status)
                .stream()
                .map(MicrocredentialEntity::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * Es desa una microcredencial al repositori persistent i transformem 
     * l’objecte de domini a entitat JPA,
     * executant la persistència i retornant l’objecte actualitzat
     * novament convertit a domini.
     */
    @Override
    public Microcredential save(Microcredential microcredential) {

        return jpaMicrocredentialRepository
                .save(MicrocredentialEntity.fromDomain(microcredential))
                .toDomain();
    }

    
}
