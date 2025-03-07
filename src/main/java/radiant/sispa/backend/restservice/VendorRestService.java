package radiant.sispa.backend.restservice;

import jakarta.persistence.EntityNotFoundException;
import radiant.sispa.backend.restdto.request.AddVendorRequestRestDTO;
import radiant.sispa.backend.restdto.response.VendorResponseDTO;

import java.util.List;
import java.util.UUID;

public interface VendorRestService {
    List<VendorResponseDTO> getAllVendor();

    void deleteVendor(UUID id) throws EntityNotFoundException;

    VendorResponseDTO addVendor(AddVendorRequestRestDTO vendorDTO, String username);
}
