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
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.travelagencyapp.exception.ResourceNotFoundException;
import pl.edu.agh.travelagencyapp.model.Trip;
import pl.edu.agh.travelagencyapp.repository.TripRepository;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class TripController {

    @Autowired
    private TripRepository tripRepository;

    @GetMapping("/trips")
    public List<Trip> getAllTrips() {
        List<Trip> trips = new ArrayList<>();
        for(Trip t: this.tripRepository.findAll())
            trips.add(new Trip(t));
        return trips;
    }

    @GetMapping("/trips/{id}")
    public ResponseEntity<Trip> getTripById(@PathVariable(value = "id") Long tripId)
            throws ResourceNotFoundException {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip with id " + tripId + " not found!"));
        return ResponseEntity.ok().body(new Trip(trip));
    }

    @PostMapping("/trips")
    public Trip createTrip(@RequestBody Trip trip) {
        return new Trip(this.tripRepository.save(trip));
    }

    @PutMapping("/trips/{id}")
    public ResponseEntity<Trip> updateTrip(@PathVariable(value = "id") Long tripId, @Valid @RequestBody Trip tripDetails)
            throws ResourceNotFoundException {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip with id " + tripId + " not found!"));

        trip.setStartDate(tripDetails.getStartDate());
        trip.setEndDate(tripDetails.getEndDate());
        trip.setAvailablePlacesNo(tripDetails.getAvailablePlacesNo());
        trip.setName(tripDetails.getName());
        trip.setCountry(tripDetails.getCountry());
        trip.setDescription(tripDetails.getDescription());
        trip.setBasePrice(tripDetails.getBasePrice());

        return ResponseEntity.ok(new Trip(this.tripRepository.save(trip)));
    }

    @DeleteMapping("/trips/{id}")
    public Map<String, Boolean> deleteTrip(@PathVariable(value = "id") Long tripId) throws ResourceNotFoundException {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip with id " + tripId + " not found!"));

        this.tripRepository.delete(trip);

        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);

        return response;
    }

    @DeleteMapping("/trips")
    public Map<String, Boolean> deleteAllTrips() {

        this.tripRepository.deleteAll();

        Map<String, Boolean> response = new HashMap<>();
        response.put("all deleted", Boolean.TRUE);

        return response;
    }

}
