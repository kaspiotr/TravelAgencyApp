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
import pl.edu.agh.travelagencyapp.exception.InvalidReservationException;
import pl.edu.agh.travelagencyapp.exception.ResourceNotFoundException;
import pl.edu.agh.travelagencyapp.model.*;
import pl.edu.agh.travelagencyapp.repository.InvoiceRepository;
import pl.edu.agh.travelagencyapp.repository.ReservationRepository;
import pl.edu.agh.travelagencyapp.repository.TripRepository;
import pl.edu.agh.travelagencyapp.repository.UserRepository;

import javax.validation.Valid;
import java.time.LocalDate;
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

    @Autowired
    private InvoiceRepository invoiceRepository;


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

    @GetMapping("/reservations/{id}/participants")
    public List<Participant> getReservationParticipants(@PathVariable(value = "id") Long reservationId)
            throws ResourceNotFoundException {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation with id " + reservationId + " not found!"));

        List<Participant> participants = new ArrayList<>();
        for(Participant p: reservation.getParticipants())
            participants.add(new Participant(p));

        return participants;
    }

    @GetMapping("/reservations/{id}/invoice")
    public ResponseEntity<Invoice> getReservationInvoice(@PathVariable(value = "id") Long reservationId)
            throws ResourceNotFoundException {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation with id " + reservationId + " not found!"));

        return ResponseEntity.ok().body(new Invoice(reservation.getInvoice()));
    }

    @PostMapping("/reservations")
    public Reservation createReservation(@RequestBody Reservation reservation, @RequestParam(value = "userId") Long userId, @RequestParam(value = "tripId") Long tripId) throws ResourceNotFoundException, InvalidReservationException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found!"));

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip with id " + tripId + " not found!"));

        if(reservation.getNumberOfParticipants() < 1)
            throw new InvalidReservationException("Invalid number of participants");

        if(trip.getStartDate().isBefore(reservation.getDate()))
            throw new InvalidReservationException("Cannot make reservation for trip in the past!");

        if(trip.countAvailablePlaces() < reservation.getNumberOfParticipants())
            throw new InvalidReservationException("Cannot make reservation - not enough available places!");

        if(!reservation.getStatus().equals("N"))
            throw new InvalidReservationException("Cannot make new reservation with status different than NEW!");

        reservation.setUser(user);
        reservation.setTrip(trip);
        reservation.setPriceSnapshot(trip.getBasePrice());
        return new Reservation(this.reservationRepository.save(reservation));
    }

    //HIDDEN
    @PutMapping("/reservations/{id}")
    public ResponseEntity<Reservation> updateReservation(@PathVariable(value = "id") Long reservationId, @Valid @RequestBody Reservation reservationDetails)
            throws ResourceNotFoundException {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation with id " + reservationId + " not found!"));

        reservation.setDate(reservationDetails.getDate());
        reservation.setStatus(reservationDetails.getStatus());
        reservation.setPriceSnapshot(reservationDetails.getPriceSnapshot());
        reservation.setNumberOfParticipants(reservationDetails.getNumberOfParticipants());

        return ResponseEntity.ok(new Reservation(this.reservationRepository.save(reservation)));
    }

    @PutMapping("/reservations/{id}/changeStatus")
    public ResponseEntity<Reservation> changeStatus(@PathVariable(value = "id") Long reservationId, @RequestParam(value = "status") String status)
            throws ResourceNotFoundException, InvalidReservationException {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation with id " + reservationId + " not found!"));

        if(!status.equals("C") && !status.equals("P") && !status.equals("N"))
            throw new InvalidReservationException("Invalid status!");

        if(reservation.getStatus().equals("C"))
            throw new InvalidReservationException("Cannot change reservation status from CANCELLED");

        if(status.equals("N"))
            throw new InvalidReservationException("Cannot change reservation status to NEW");

        if(status.equals("C"))
            reservation.setNumberOfParticipants(0);

        if(reservation.getStatus().equals("N") && status.equals("P")){
            if(reservation.getNumberOfParticipants() != reservation.getParticipants().size())
                throw new InvalidReservationException("Actual number of participants not matching declared number!");
            Invoice invoice = new Invoice(LocalDate.now(), reservation.countTotalPrice());
            invoice.setReservation(reservation);
            reservation.setInvoice(this.invoiceRepository.save(invoice));
        }

        reservation.setStatus(status);

        return ResponseEntity.ok(new Reservation(this.reservationRepository.save(reservation)));
    }

    @PutMapping("/reservations/{id}/changeNoParticipants")
    public ResponseEntity<Reservation> changeNoParticipants(@PathVariable(value = "id") Long reservationId, @RequestParam(value = "number") Long number)
            throws ResourceNotFoundException, InvalidReservationException {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation with id " + reservationId + " not found!"));

        if(number < 1)
            throw new InvalidReservationException("Invalid number of participants!");

        if(number - reservation.getNumberOfParticipants() > reservation.getTrip().countAvailablePlaces())
            throw new InvalidReservationException("Cannot change number of participants. Too few places available!");

        if(reservation.getParticipants().size() > number)
            throw new InvalidReservationException("Cannot change number of participants. Too many participants actually declared!");

        reservation.setNumberOfParticipants(number.intValue());

        return ResponseEntity.ok(new Reservation(this.reservationRepository.save(reservation)));
    }

    //HIDDEN
    @DeleteMapping("/reservations/{id}")
    public Map<String, Boolean> deleteReservation(@PathVariable(value = "id") Long reservationId) throws ResourceNotFoundException {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation with id " + reservationId + " not found!"));

        this.reservationRepository.delete(reservation);

        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);

        return response;
    }

    //HIDDEN
    @DeleteMapping("/reservations")
    public Map<String, Boolean> deleteAllReservations() {

        this.reservationRepository.deleteAll();

        Map<String, Boolean> response = new HashMap<>();
        response.put("all deleted", Boolean.TRUE);

        return response;
    }

}
