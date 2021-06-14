package pl.edu.agh.travelagencyapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.agh.travelagencyapp.model.Participant;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
}
