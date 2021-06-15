package pl.edu.agh.travelagencyapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.travelagencyapp.exception.InvalidTripException;
import pl.edu.agh.travelagencyapp.exception.ResourceNotFoundException;
import pl.edu.agh.travelagencyapp.model.ExtendedTrip;
import pl.edu.agh.travelagencyapp.model.Reservation;
import pl.edu.agh.travelagencyapp.model.Trip;
import pl.edu.agh.travelagencyapp.repository.TripRepository;

import javax.validation.Valid;
import java.time.LocalDate;
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

    @GetMapping("/trips/search")
    public List<Trip> getFilteredTrips(@RequestParam(value = "country", required = false) String country,
                                       @RequestParam(value = "priceFrom", required = false) Long priceFrom,
                                       @RequestParam(value = "priceTo", required = false) Long priceTo,
                                       @RequestParam(value = "placesFrom", required = false) Long placesFrom,
                                       @RequestParam(value = "placesTo", required = false) Long placesTo,
                                       @RequestParam(value = "duration", required = false) Long duration,
                                       @RequestParam(value = "dateFrom", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate dateFrom,
                                       @RequestParam(value = "dateTo", required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate dateTo) {
        List<Trip> trips = new ArrayList<>();
        for(Trip t: this.tripRepository.findAll()){
            if((priceFrom == null || priceFrom <= t.getBasePrice()) &&
                    (priceTo == null || priceTo >= t.getBasePrice()) &&
                    (country == null || country.equals(t.getCountry())) &&
                    (placesFrom == null || placesFrom <= t.countAvailablePlaces()) &&
                    (placesTo == null || placesTo >= t.countAvailablePlaces()) &&
                    (dateFrom == null || !dateFrom.isAfter(t.getStartDate())) &&
                    (dateTo == null || !dateTo.isBefore(t.getEndDate())) &&
                    (duration == null || t.getStartDate().plusDays(duration).equals(t.getEndDate()))
            ){
                trips.add(new Trip(t));
            }
        }
        return trips;
    }

    @GetMapping("/trips/{id}")
    public ResponseEntity<Trip> getTripById(@PathVariable(value = "id") Long tripId)
            throws ResourceNotFoundException {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip with id " + tripId + " not found!"));
        return ResponseEntity.ok().body(new Trip(trip));
    }

    @GetMapping("/trips/{id}/reservations")
    public List<Reservation> getTripReservations(@PathVariable(value = "id") Long tripId)
            throws ResourceNotFoundException {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip with id " + tripId + " not found!"));

        List<Reservation> reservations = new ArrayList<>();
        for (Reservation r: trip.getReservations())
            reservations.add(new Reservation(r));
        return reservations;
    }

    @GetMapping("/trips/withPlaces")
    public List<ExtendedTrip> getAllExtendedTrips() {
        List<ExtendedTrip> trips = new ArrayList<>();
        for(Trip t: this.tripRepository.findAll()){
            trips.add(new ExtendedTrip(t));
        }
        return trips;
    }

    @GetMapping("/trips/withPlaces/{id}")
    public ResponseEntity<ExtendedTrip> getExtendedTripById(@PathVariable(value = "id") Long tripId)
            throws ResourceNotFoundException {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip with id " + tripId + " not found!"));
        return ResponseEntity.ok().body(new ExtendedTrip(trip));
    }

    @PostMapping("/trips")
    public Trip createTrip(@RequestBody Trip trip) throws InvalidTripException {
        if(trip.getBasePrice() < 1)
            throw new InvalidTripException("Cannot create trip. Invalid base price!");

        if(trip.getAvailablePlacesNo() < 1)
            throw new InvalidTripException("Cannot create trip. Invalid number of available places!");

        if(trip.getStartDate().isAfter(trip.getEndDate()))
            throw new InvalidTripException("Cannot create trip. Start date cannot be after end date");

        return new Trip(this.tripRepository.save(trip));
    }

    //HIDDEN
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

    //HIDDEN
    @DeleteMapping("/trips/{id}")
    public Map<String, Boolean> deleteTrip(@PathVariable(value = "id") Long tripId) throws ResourceNotFoundException {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip with id " + tripId + " not found!"));

        this.tripRepository.delete(trip);

        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);

        return response;
    }

    //HIDDEN
    @DeleteMapping("/trips")
    public Map<String, Boolean> deleteAllTrips() {

        this.tripRepository.deleteAll();

        Map<String, Boolean> response = new HashMap<>();
        response.put("all deleted", Boolean.TRUE);

        return response;
    }

}
