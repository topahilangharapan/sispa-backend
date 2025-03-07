package radiant.sispa.backend.restservice;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import radiant.sispa.backend.model.Vendor;
import radiant.sispa.backend.repository.VendorDb;
import radiant.sispa.backend.restdto.request.AddVendorRequestRestDTO;
import radiant.sispa.backend.restdto.response.VendorResponseDTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class VendorRestServiceImpl implements VendorRestService {

    @Autowired
    VendorDb vendorDb;

    @Override
    public List<VendorResponseDTO> getAllVendor() {
        List<Vendor> listVendor = vendorDb.findByDeletedAtNull();

        List<VendorResponseDTO> listVendorResponseDTO = new ArrayList<>();
        for (Vendor vendor : listVendor) {
            var vendorResponseDTO = vendorToVendorResponseDTO(vendor);
            listVendorResponseDTO.add(vendorResponseDTO);
        }
        return listVendorResponseDTO;
    }

    @Override
    public void deleteVendor(UUID id) throws EntityNotFoundException {
        Vendor vendorToDelete = vendorDb.findByIdAndDeletedAtNull(id);

        if (vendorToDelete == null) {
            throw new EntityNotFoundException("Vendor not found");
        }

        vendorToDelete.setDeletedAt(new Date());
        vendorDb.save(vendorToDelete);
    }

    @Override
    public VendorResponseDTO addVendor(AddVendorRequestRestDTO vendorDTO, String username) {
        Vendor newVendor = new Vendor();

        newVendor.setId(UUID.randomUUID());
        newVendor.setName(vendorDTO.getName());
        newVendor.setEmail(vendorDTO.getEmail());
        newVendor.setAddress(vendorDTO.getAddress());
        newVendor.setContact(vendorDTO.getContact());
        newVendor.setService(vendorDTO.getService());
        newVendor.setCreatedAt(new Date());
        newVendor.setUpdatedAt(new Date());
        newVendor.setUpdatedBy(username);
        newVendor.setCreatedBy(username);

        vendorDb.save(newVendor);
        return vendorToVendorResponseDTO(newVendor);
    }

    private VendorResponseDTO vendorToVendorResponseDTO(Vendor vendor) {
        VendorResponseDTO vendorResponseDTO = new VendorResponseDTO();

        vendorResponseDTO.setId(vendor.getId());
        vendorResponseDTO.setName(vendor.getName());
        vendorResponseDTO.setContact(vendor.getContact());
        vendorResponseDTO.setAddress(vendor.getAddress());
        vendorResponseDTO.setEmail(vendor.getEmail());
        vendorResponseDTO.setService(vendor.getService());
        vendorResponseDTO.setCreatedAt(vendor.getCreatedAt());
        vendorResponseDTO.setUpdatedAt(vendor.getUpdatedAt());
        vendorResponseDTO.setCreatedBy(vendor.getCreatedBy());
        vendorResponseDTO.setUpdatedBy(vendor.getUpdatedBy());
        vendorResponseDTO.setDeletedAt(vendor.getDeletedAt());
        return vendorResponseDTO;
    }

}
