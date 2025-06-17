package radiant.sispa.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import radiant.sispa.backend.model.Client;
import radiant.sispa.backend.model.Item;

import java.util.List;

@Repository
public interface ItemDb extends JpaRepository<Item, Long> {
    List<Item> findByDeletedAtNull();

    Item findByIdAndDeletedAtNull(Long id);

    Item findByTitleIgnoreCaseAndDeletedAtNull(String title);
}
