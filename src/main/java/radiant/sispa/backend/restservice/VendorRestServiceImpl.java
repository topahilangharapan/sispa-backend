package radiant.sispa.backend.restservice;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import radiant.sispa.backend.model.Vendor;
import radiant.sispa.backend.repository.VendorDb;
import radiant.sispa.backend.restdto.response.VendorResponseDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class VendorRestServiceImpl implements VendorRestService {

    @Autowired
    VendorDb vendorDb;

    @Override
    public List<VendorResponseDTO> getAllVendor() {
        List<Vendor> listVendor = vendorDb.findAll();

        List<VendorResponseDTO> listVendorResponseDTO = new ArrayList<>();
        for (Vendor vendor : listVendor) {
            var vendorResponseDTO = vendorToVendorResponseDTO(vendor);
            listVendorResponseDTO.add(vendorResponseDTO);
        }
        return listVendorResponseDTO;
    }

    @Override
    public void deleteVendor(UUID id) throws EntityNotFoundException {
        Vendor vendorToDelete = vendorDb.findByIdAndIsDeletedFalse(id);

        if (vendorToDelete == null) {
            throw new EntityNotFoundException("Vendor not found");
        }

        vendorToDelete.setIsDeleted(true);
        vendorDb.save(vendorToDelete);
    }

    private VendorResponseDTO vendorToVendorResponseDTO(Vendor vendor) {
        VendorResponseDTO vendorResponseDTO = new VendorResponseDTO();

        vendorResponseDTO.setId(vendor.getId());
        vendorResponseDTO.setName(vendor.getName());
        vendorResponseDTO.setContact(vendor.getContact());
        vendorResponseDTO.setAddress(vendor.getAddress());
        vendorResponseDTO.setEmail(vendor.getEmail());
        vendorResponseDTO.setCreatedAt(vendor.getCreatedAt());
        vendorResponseDTO.setUpdatedAt(vendor.getUpdatedAt());
        vendorResponseDTO.setIsDeleted(vendor.getIsDeleted());
        return vendorResponseDTO;
    }

}
