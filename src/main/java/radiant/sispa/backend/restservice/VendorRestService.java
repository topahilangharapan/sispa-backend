package radiant.sispa.backend.restservice;

import jakarta.persistence.EntityNotFoundException;
import radiant.sispa.backend.model.Vendor;
import radiant.sispa.backend.restdto.request.AddVendorRequestRestDTO;
import radiant.sispa.backend.restdto.request.UpdateVendorRequestRestDTO;
import radiant.sispa.backend.restdto.response.VendorResponseDTO;

import java.util.List;


public interface VendorRestService {
    List<VendorResponseDTO> getAllVendor();

    void deleteVendor(String id) throws EntityNotFoundException;

    VendorResponseDTO addVendor(AddVendorRequestRestDTO vendorDTO, String username);

    VendorResponseDTO updateVendor(String id, UpdateVendorRequestRestDTO vendorDTO, String username);

    VendorResponseDTO getVendorById(String id);

    Vendor addPurchaseOrder(String idVendor, Long idPo);
}
