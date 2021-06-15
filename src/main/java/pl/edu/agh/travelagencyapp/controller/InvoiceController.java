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
import pl.edu.agh.travelagencyapp.model.Invoice;
import pl.edu.agh.travelagencyapp.model.Reservation;
import pl.edu.agh.travelagencyapp.repository.InvoiceRepository;
import pl.edu.agh.travelagencyapp.repository.ReservationRepository;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class InvoiceController {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @GetMapping("/invoices")
    public List<Invoice> getAllInvoices() {
        List<Invoice> invoices = new ArrayList<>();
        for (Invoice invoice : this.invoiceRepository.findAll()) {
            invoices.add(new Invoice(invoice));
        }
        return invoices;
    }

    @GetMapping("/invoices/{id}")
    public ResponseEntity<Invoice> getInvoiceById(@PathVariable(value = "id") Long invoiceId)
            throws ResourceNotFoundException {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice with id " + invoiceId + " not found!"));
        return ResponseEntity.ok().body(new Invoice(invoice));
    }

    //HIDDEN
    @PostMapping("/invoices")
    public Invoice createInvoice(@RequestBody Invoice invoice, @RequestParam(value = "reservationId") Long reservationId)
            throws ResourceNotFoundException {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation with id " + reservationId + " not found!"));

        invoice.setReservation(reservation);

        return new Invoice(this.invoiceRepository.save(invoice));
    }

    //HIDDEN
    @PutMapping("/invoices/{id}")
    public ResponseEntity<Invoice> updateInvoice(@PathVariable(value = "id") Long invoiceId, @Valid @RequestBody Invoice invoiceDetails)
            throws ResourceNotFoundException {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice with id " + invoiceId + " not found!"));

        invoice.setDate(invoiceDetails.getDate());
        invoice.setTotalPrice(invoiceDetails.getTotalPrice());

        return ResponseEntity.ok(new Invoice(this.invoiceRepository.save(invoice)));
    }

    //HIDDEN
    @DeleteMapping("/invoices/{id}")
    public Map<String, Boolean> deleteInvoice(@PathVariable(value = "id") Long invoiceId)
            throws ResourceNotFoundException {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice with id " + invoiceId + " not found!"));

        this.invoiceRepository.delete(invoice);

        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);

        return response;
    }

    //HIDDEN
    @DeleteMapping("/invoices")
    public Map<String, Boolean> deleteAllInvoices() {

        this.invoiceRepository.deleteAll();

        Map<String, Boolean> response = new HashMap<>();
        response.put("all deleted", Boolean.TRUE);

        return response;
    }

}
