package pl.edu.agh.travelagencyapp.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "RESERVATIONS", schema = "public")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "status", nullable = false, columnDefinition = "varchar(1)")
    private String status;

    @Column(name = "price_snapshot", nullable = false)
    private int priceSnapshot;

    @Column(name = "number_of_participants", nullable = false)
    private int numberOfParticipants;

    @OneToMany
    @JoinColumn(name = "reservation_id")
    private Set<Participant> participants;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "trip_id")
    private Trip trip;

    @OneToOne(mappedBy = "reservation")
    private Invoice invoice;

    public Reservation(){
        this.participants = new HashSet<>();
        this.invoice = null;
    }

    public Reservation(LocalDate date, String status, int priceSnapshot, int numberOfParticipants, User user, Trip trip) {
        this.date = date;
        this.status = status;
        this.priceSnapshot = priceSnapshot;
        this.numberOfParticipants = numberOfParticipants;
        this.user = user;
        this.trip = trip;
        this.participants = new HashSet<>();
        this.invoice = null;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPriceSnapshot() {
        return priceSnapshot;
    }

    public void setPriceSnapshot(int priceSnapshot) {
        this.priceSnapshot = priceSnapshot;
    }

    public int getNumberOfParticipants() {
        return numberOfParticipants;
    }

    public void setNumberOfParticipants(int numberOfParticipants) {
        this.numberOfParticipants = numberOfParticipants;
    }

    public Set<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<Participant> participants) {
        this.participants = participants;
    }

    public void addParticipant(Participant participant){
        this.participants.add(participant);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }
}
