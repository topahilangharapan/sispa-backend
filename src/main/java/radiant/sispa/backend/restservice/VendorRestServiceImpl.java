package radiant.sispa.backend.restservice;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jdk.swing.interop.SwingInterOpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import radiant.sispa.backend.model.Vendor;
import radiant.sispa.backend.repository.VendorDb;
import radiant.sispa.backend.restdto.request.AddVendorRequestRestDTO;
import radiant.sispa.backend.restdto.request.UpdateVendorRequestRestDTO;
import radiant.sispa.backend.restdto.response.VendorResponseDTO;
import radiant.sispa.backend.security.jwt.JwtUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class VendorRestServiceImpl implements VendorRestService {

    @Autowired
    VendorDb vendorDb;

    @Autowired
    private JwtUtils jwtUtils;

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
    public VendorResponseDTO addVendor(AddVendorRequestRestDTO vendorDTO, String authHeader) throws IllegalArgumentException {
        String token = authHeader.substring(7);
        String username = jwtUtils.getUserNameFromJwtToken(token);

        List<Vendor> existingVendor = vendorDb.findByNameAndContact(vendorDTO.getName(), vendorDTO.getContact());

        for (var vendor : existingVendor) {
            if (vendor.getName().equals(vendorDTO.getName()) && vendor.getContact().equals(vendorDTO.getContact())) {
                throw new IllegalArgumentException("Vendor dengan nama dan kontak ini sudah terdaftar.");
            }
        }

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

    @Override
    public VendorResponseDTO updateVendor(String id, UpdateVendorRequestRestDTO vendorDTO, String authHeader) {
        String token = authHeader.substring(7);
        String username = jwtUtils.getUserNameFromJwtToken(token);

        Vendor vendor = vendorDb.findById(id).orElse(null);
        if (vendor == null || vendor.getDeletedAt() != null){
            return null;
        }

        vendor.setName(vendorDTO.getName());
        vendor.setContact(vendorDTO.getContact());
        vendor.setAddress(vendorDTO.getAddress());
        vendor.setEmail(vendorDTO.getEmail());
        vendor.setService(vendorDTO.getService());
        vendor.setDescription(vendorDTO.getDescription());
        vendor.setUpdatedBy(username);

        Vendor updatedVendor = vendorDb.save(vendor);
        return vendorToVendorResponseDTO(updatedVendor);
    }

    @Override
    public VendorResponseDTO getVendorById(String id) {
        Vendor vendor = vendorDb.findById(id).orElse(null);

        if (vendor == null || vendor.getDeletedAt() != null){
            return null;
        }

        return vendorToVendorResponseDTO(vendor);
    }

}
