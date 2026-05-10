package edu.uoc.epcsd.microcredential.domain.service;

import edu.uoc.epcsd.microcredential.domain.Microcredential;
import java.util.List;
import java.util.Optional;
import javax.validation.constraints.NotNull;

public interface MicrocredentialService {

	Optional<Microcredential> getMicrocredentialById(@NotNull Long microcredentialId);
	
	List<Microcredential> getPendingMicrocredentialRequests();

	Boolean rejectPendingMicrocredential(@NotNull Long microcredentialId);

	Boolean requestCourseMicrocredentials(@NotNull Long courseId);

	Boolean approvePendingMicrocredential(@NotNull Long microcredentialId);
}
