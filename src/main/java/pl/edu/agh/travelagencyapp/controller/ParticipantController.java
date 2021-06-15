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
import pl.edu.agh.travelagencyapp.exception.InvalidParticipantException;
import pl.edu.agh.travelagencyapp.exception.ResourceNotFoundException;
import pl.edu.agh.travelagencyapp.model.Participant;
import pl.edu.agh.travelagencyapp.model.Reservation;
import pl.edu.agh.travelagencyapp.model.Trip;
import pl.edu.agh.travelagencyapp.model.User;
import pl.edu.agh.travelagencyapp.repository.ParticipantRepository;
import pl.edu.agh.travelagencyapp.repository.ReservationRepository;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class ParticipantController {

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @GetMapping("/participants")
    public List<Participant> getAllParticipants() {
        List<Participant> participants = new ArrayList<>();
        for (Participant participant : participantRepository.findAll()) {
            participants.add(new Participant(participant));
        }
        return participants;
    }

    @GetMapping("/participants/{id}")
    public ResponseEntity<Participant> getParticipantById(@PathVariable(value = "id") Long participantId)
            throws ResourceNotFoundException {
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new ResourceNotFoundException("Participant with id " + participantId + " not found!"));
        return ResponseEntity.ok().body(new Participant(participant));
    }

    @GetMapping("/participants/{id}/reservation")
    public ResponseEntity<Reservation> getParticipantReservation(@PathVariable(value = "id") Long participantId)
            throws ResourceNotFoundException {
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new ResourceNotFoundException("Participant with id " + participantId + " not found!"));
        return ResponseEntity.ok().body(new Reservation(participant.getReservation()));
    }

    @GetMapping("/participants/{id}/user")
    public ResponseEntity<User> getParticipantUser(@PathVariable(value = "id") Long participantId)
            throws ResourceNotFoundException {
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new ResourceNotFoundException("Participant with id " + participantId + " not found!"));
        return ResponseEntity.ok().body(new User(participant.getReservation().getUser()));
    }

    @GetMapping("/participants/{id}/trip")
    public ResponseEntity<Trip> getParticipantTrip(@PathVariable(value = "id") Long participantId)
            throws ResourceNotFoundException {
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new ResourceNotFoundException("Participant with id " + participantId + " not found!"));
        return ResponseEntity.ok().body(new Trip(participant.getReservation().getTrip()));
    }

    @PostMapping("/participants")
    public Participant createParticipant(@RequestBody Participant participant, @RequestParam(value = "reservationId") Long reservationId)
            throws ResourceNotFoundException, InvalidParticipantException {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation with id " + reservationId + " not found!"));

        if(reservation.getParticipants().size() >= reservation.getNumberOfParticipants())
            throw new InvalidParticipantException("Cannot add participant. Number of participants is as declared");

        participant.setReservation(reservation);

        return new Participant(this.participantRepository.save(participant));
    }

    @PutMapping("/participants/{id}")
    public ResponseEntity<Participant> updateParticipant(@PathVariable(value = "id") Long participantId, @Valid @RequestBody Participant participantDetails)
            throws ResourceNotFoundException {
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new ResourceNotFoundException("Participant with id " + participantId + " not found!"));

        participant.setFirstName(participantDetails.getFirstName());
        participant.setLastName(participantDetails.getLastName());
        participant.setBirthDate(participantDetails.getBirthDate());

        return ResponseEntity.ok(new Participant(this.participantRepository.save(participant)));
    }

    @DeleteMapping("/participants/{id}")
    public Map<String, Boolean> deleteParticipant(@PathVariable(value = "id") Long participantId)
            throws ResourceNotFoundException, InvalidParticipantException {
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new ResourceNotFoundException("Participant with id " + participantId + " not found!"));

        if(!participant.getReservation().getStatus().equals("N"))
            throw new InvalidParticipantException("Cannot delete participant. Reservation status is not NEW");

        this.participantRepository.delete(participant);

        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);

        return response;
    }

    //HIDDEN
    @DeleteMapping("/participants")
    public Map<String, Boolean> deleteAllParticipants() {

        this.participantRepository.deleteAll();

        Map<String, Boolean> response = new HashMap<>();
        response.put("all deleted", Boolean.TRUE);

        return response;
    }

}
