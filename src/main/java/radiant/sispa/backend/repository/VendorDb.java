package radiant.sispa.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import radiant.sispa.backend.model.Vendor;

import java.util.List;
import java.util.UUID;

@Repository
public interface VendorDb extends JpaRepository<Vendor, UUID> {
    List<Vendor> findByDeletedAtNull();

    Vendor findByIdAndIsDeletedFalse(UUID id);
}
