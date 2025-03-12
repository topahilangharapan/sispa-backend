package radiant.sispa.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import radiant.sispa.backend.model.PurchaseOrderItem;
import radiant.sispa.backend.model.UserModel;

@Repository
public interface PurchaseOrderItemDb extends JpaRepository<PurchaseOrderItem, Long> {

}
