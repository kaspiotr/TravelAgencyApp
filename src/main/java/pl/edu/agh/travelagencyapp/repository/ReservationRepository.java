package pl.edu.agh.travelagencyapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.agh.travelagencyapp.model.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
