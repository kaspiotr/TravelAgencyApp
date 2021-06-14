package pl.edu.agh.travelagencyapp.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "TRIPS", schema = "public")
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "available_places_no", nullable = false)
    private int availablePlacesNo;

    @Column(name = "name", nullable = false, columnDefinition = "varchar(50)")
    private String name;

    @Column(name = "country", nullable = false, columnDefinition = "varchar(50)")
    private String country;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "base_price", nullable = false)
    private int basePrice;

    @OneToMany
    @JoinColumn(name = "trip_id")
    private Set<Reservation> reservations;

    public Trip(){
        this.reservations = new HashSet<>();
    }

    public Trip(LocalDate startDate, LocalDate endDate, int availablePlacesNo, String name, String country, String description, int basePrice) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.availablePlacesNo = availablePlacesNo;
        this.name = name;
        this.country = country;
        this.description = description;
        this.basePrice = basePrice;
        this.reservations = new HashSet<>();
    }

    public Trip(Trip trip){
        this.id = trip.id;
        this.startDate = trip.startDate;
        this.endDate = trip.endDate;
        this.availablePlacesNo = trip.availablePlacesNo;
        this.name = trip.name;
        this.country = trip.country;
        this.description = trip.description;
        this.basePrice = trip.basePrice;
        this.reservations = new HashSet<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public int getAvailablePlacesNo() {
        return availablePlacesNo;
    }

    public void setAvailablePlacesNo(int availablePlacesNo) {
        this.availablePlacesNo = availablePlacesNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(int basePrice) {
        this.basePrice = basePrice;
    }

    public Set<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(Set<Reservation> reservations) {
        this.reservations = reservations;
    }

    public void addReservation(Reservation reservation){
        this.reservations.add(reservation);
    }
}
