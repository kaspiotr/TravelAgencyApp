package pl.edu.agh.travelagencyapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.travelagencyapp.exception.ResourceNotFoundException;
import pl.edu.agh.travelagencyapp.model.Reservation;
import pl.edu.agh.travelagencyapp.model.Trip;
import pl.edu.agh.travelagencyapp.model.User;
import pl.edu.agh.travelagencyapp.repository.ReservationRepository;
import pl.edu.agh.travelagencyapp.repository.TripRepository;
import pl.edu.agh.travelagencyapp.repository.UserRepository;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class ReservationController {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/reservations")
    public List<Reservation> getAllReservations() {
        List<Reservation> reservations = new ArrayList<>();
        for(Reservation res: this.reservationRepository.findAll())
            reservations.add(new Reservation(res));
        return reservations;
    }

    @GetMapping("/reservations/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable(value = "id") Long reservationId)
            throws ResourceNotFoundException {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation with id " + reservationId + " not found!"));

        return ResponseEntity.ok().body(new Reservation(reservation));
    }

    @PostMapping("/reservations")
    public Reservation createReservation(@RequestBody Reservation reservation, @RequestParam(value = "userId") Long userId, @RequestParam(value = "tripId") Long tripId) throws ResourceNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found!"));

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip with id " + tripId + " not found!"));

        reservation.setUser(user);
        reservation.setTrip(trip);
        return new Reservation(this.reservationRepository.save(reservation));
    }

    @PutMapping("/reservations/{id}")
    public ResponseEntity<Reservation> updateUser(@PathVariable(value = "id") Long reservationId, @Valid @RequestBody Reservation reservationDetails)
            throws ResourceNotFoundException {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation with id " + reservationId + " not found!"));

        reservation.setDate(reservationDetails.getDate());
        reservation.setStatus(reservationDetails.getStatus());
        reservation.setPriceSnapshot(reservationDetails.getPriceSnapshot());
        reservation.setNumberOfParticipants(reservationDetails.getNumberOfParticipants());

        return ResponseEntity.ok(new Reservation(this.reservationRepository.save(reservation)));
    }

    @DeleteMapping("/reservations/{id}")
    public Map<String, Boolean> deleteReservation(@PathVariable(value = "id") Long reservationId) throws ResourceNotFoundException {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation with id " + reservationId + " not found!"));

        this.reservationRepository.delete(reservation);

        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);

        return response;
    }

    @DeleteMapping("/reservations")
    public Map<String, Boolean> deleteAllReservations() {

        this.reservationRepository.deleteAll();

        Map<String, Boolean> response = new HashMap<>();
        response.put("all deleted", Boolean.TRUE);

        return response;
    }

}
