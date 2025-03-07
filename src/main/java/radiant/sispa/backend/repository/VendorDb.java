package radiant.sispa.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import radiant.sispa.backend.model.Vendor;

import java.util.List;
import java.util.UUID;


public interface VendorDb extends JpaRepository<Vendor, UUID> {
    List<Vendor> findByDeletedAtNull();

    Vendor findByIdAndDeletedAtNull(UUID id);
}
