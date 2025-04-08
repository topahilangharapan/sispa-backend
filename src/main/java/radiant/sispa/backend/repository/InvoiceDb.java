package radiant.sispa.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import radiant.sispa.backend.model.Invoice;

import java.time.Instant;
import java.util.List;

public interface InvoiceDb extends JpaRepository<Invoice, String> {
    @Query("SELECT p FROM Invoice p WHERE p.createdAt >= :startOfDay AND p.createdAt < :endOfDay")
    List<Invoice> findInvoicesToday(@Param("startOfDay") Instant startOfDay, @Param("endOfDay") Instant endOfDay);
    Invoice findByIdAndDeletedAtNull(Long id);
    List<Invoice> findAllByDeletedAtIsNullAndPurchaseOrderDeletedAtIsNull();
}
