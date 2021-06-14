package pl.edu.agh.travelagencyapp.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "INVOICES", schema = "public")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "total_price", nullable = false)
    private int totalPrice;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "reservation_id", referencedColumnName = "id")
    private Reservation reservation;

    public Invoice(){}

    public Invoice(LocalDate date, int totalPrice, Reservation reservation) {
        this.date = date;
        this.totalPrice = totalPrice;
        this.reservation = reservation;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }
}
