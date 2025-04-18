package radiant.sispa.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import radiant.sispa.backend.model.Item;

@Repository
public interface ItemDb extends JpaRepository<Item, Long> {

}
