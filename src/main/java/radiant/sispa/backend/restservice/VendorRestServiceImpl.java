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
    public void deleteVendor(String id) throws EntityNotFoundException {
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

        newVendor.setId(generateVendorId(vendorDTO.getName()));
        newVendor.setName(vendorDTO.getName());
        newVendor.setEmail(vendorDTO.getEmail());
        newVendor.setAddress(vendorDTO.getAddress());
        newVendor.setContact(vendorDTO.getContact());
        newVendor.setService(vendorDTO.getService());
        newVendor.setDescription(vendorDTO.getDescription());
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
        vendorResponseDTO.setDescription(vendor.getDescription());
        vendorResponseDTO.setCreatedAt(vendor.getCreatedAt());
        vendorResponseDTO.setUpdatedAt(vendor.getUpdatedAt());
        vendorResponseDTO.setCreatedBy(vendor.getCreatedBy());
        vendorResponseDTO.setUpdatedBy(vendor.getUpdatedBy());
        vendorResponseDTO.setDeletedAt(vendor.getDeletedAt());
        return vendorResponseDTO;
    }

    public String generateVendorId(String vendorName) {
        String prefix = "VEN";

        String[] nameParts = vendorName.split(" ");
        String vendorInitials;

        if (nameParts.length > 1) {
            vendorInitials = (nameParts[0].substring(0, 2) + nameParts[1].substring(0, 2)).toUpperCase();
        } else {
            vendorInitials = nameParts[0].substring(0, 3).toUpperCase();
        }

        int totalVendor = vendorDb.findAll().size();
        String vendorNumber = String.format("%03d", totalVendor + 1);

        return prefix + vendorInitials + vendorNumber;
    }

}
