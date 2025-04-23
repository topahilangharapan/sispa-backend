package radiant.sispa.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import radiant.sispa.backend.model.PurchaseOrder;
import radiant.sispa.backend.model.PurchaseOrderItem;

import java.time.Instant;
import java.util.List;

@Repository
public interface PurchaseOrderItemDb extends JpaRepository<PurchaseOrderItem, Long> {

}
