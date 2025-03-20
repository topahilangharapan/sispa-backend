package radiant.sispa.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import radiant.sispa.backend.model.PurchaseOrder;
import radiant.sispa.backend.model.Role;
import radiant.sispa.backend.model.UserModel;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface PurchaseOrderDb extends JpaRepository<PurchaseOrder, Long> {

    @Query("SELECT p FROM PurchaseOrder p WHERE p.createdAt >= :startOfDay AND p.createdAt < :endOfDay")
    List<PurchaseOrder> findPurchaseOrdersToday(@Param("startOfDay") Instant startOfDay, @Param("endOfDay") Instant endOfDay);

    PurchaseOrder findPurchaseOrderById(Long id);
    PurchaseOrder findPurchaseOrderByNoPo(String id);
}
