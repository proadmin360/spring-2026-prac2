package edu.uoc.epcsd.microcredential.infrastructure.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import edu.uoc.epcsd.microcredential.domain.MicrocredentialStatus;
import java.util.List;

@Repository
public interface SpringDataMicrocredentialRepository extends JpaRepository<MicrocredentialEntity, Long> {

	public Optional<MicrocredentialEntity> getMicrocredentialById(Long id);
	
	List<MicrocredentialEntity> findByStatus(MicrocredentialStatus status);
}
