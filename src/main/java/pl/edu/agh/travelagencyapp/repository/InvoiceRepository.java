package pl.edu.agh.travelagencyapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.agh.travelagencyapp.model.Invoice;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
}
