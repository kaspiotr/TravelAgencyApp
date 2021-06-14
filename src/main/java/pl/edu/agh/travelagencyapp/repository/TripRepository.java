package pl.edu.agh.travelagencyapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.agh.travelagencyapp.model.Trip;

public interface TripRepository extends JpaRepository<Trip, Long> {
}
