package pl.edu.agh.travelagencyapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.travelagencyapp.exception.ResourceNotFoundException;
import pl.edu.agh.travelagencyapp.model.Trip;
import pl.edu.agh.travelagencyapp.repository.TripRepository;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class TripController {

    @Autowired
    private TripRepository tripRepository;

    @GetMapping("trips")
    public List<Trip> getAllTrips() {
        return this.tripRepository.findAll();
    }

    @GetMapping("/trips/{id}")
    public ResponseEntity<Trip> getTripById(@PathVariable(value = "id") Long tripId)
            throws ResourceNotFoundException {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip with id " + tripId + " not found!"));
        return ResponseEntity.ok().body(trip);
    }

    @PostMapping("trips")
    public Trip createTrip(@RequestBody Trip trip) {
        return this.tripRepository.save(trip);
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

        return ResponseEntity.ok(this.tripRepository.save(trip));
    }

    @DeleteMapping("trips/{id}")
    public Map<String, Boolean> deleteTrip(@PathVariable(value = "id") Long tripId) throws ResourceNotFoundException {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip with id " + tripId + " not found!"));

        this.tripRepository.delete(trip);

        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);

        return response;
    }
}
