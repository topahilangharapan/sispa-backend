package radiant.sispa.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import radiant.sispa.backend.model.Client;
import radiant.sispa.backend.model.Vendor;

import java.util.List;

public interface ClientDb extends JpaRepository<Client, String> {
    List<Client> findByDeletedAtNull();

    Client findByIdAndDeletedAtNull(String id);

    List<Client> findByNameAndContactAndDeletedAtNull(String name, String contact);
}
