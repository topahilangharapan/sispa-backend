package radiant.sispa.backend.restservice;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import radiant.sispa.backend.model.Item;
import radiant.sispa.backend.model.ItemStatus;
import radiant.sispa.backend.model.WorkExperienceCategory;
import radiant.sispa.backend.repository.ItemStatusDb;
import radiant.sispa.backend.repository.WorkExperienceCategoryDb;
import radiant.sispa.backend.restdto.request.CreateItemStatusRequestDTO;
import radiant.sispa.backend.restdto.request.CreateWorkExperienceCategoryRequestDTO;
import radiant.sispa.backend.restdto.response.CreateItemStatusResponseDTO;
import radiant.sispa.backend.restdto.response.CreateWorkExperienceCategoryResponseDTO;
import radiant.sispa.backend.restdto.response.GenericDataDTO;
import radiant.sispa.backend.security.jwt.JwtUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ItemStatusServiceImpl implements ItemStatusService {
    @Autowired
    private ItemStatusDb itemStatusDb;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public List<ItemStatus> getAllItemStatuses() {
        return itemStatusDb.findAll();
    }

    @Override
    public ItemStatus getItemStatusByName(String name) throws EntityNotFoundException {
        ItemStatus itemStatus = itemStatusDb.findByStatus(name.toUpperCase()).orElse(null);

        if(itemStatus == null) {
            throw new EntityNotFoundException(String.format("Item Status %s doesnt exist!", name));
        }

        return itemStatus;
    }

    @Override
    public CreateItemStatusResponseDTO addItemStatus(CreateItemStatusRequestDTO requestDTO, String authHeader) throws EntityExistsException {
        String token = authHeader.substring(7);
        String createdBy = jwtUtils.getUserNameFromJwtToken(token);

        ItemStatus itemStatus = new ItemStatus();

        if (itemStatusDb.findByStatus(requestDTO.getName().toUpperCase()).orElse(null) != null) {
            throw new EntityExistsException(String.format("Item Status %s already exists", requestDTO.getName()));
        }

        itemStatus.setStatus(requestDTO.getName().toUpperCase());
        itemStatus.setCreatedBy(createdBy);
        itemStatusDb.save(itemStatus);

        CreateItemStatusResponseDTO responseDTO = new CreateItemStatusResponseDTO();
        responseDTO.setId(itemStatus.getId());
        responseDTO.setName(itemStatus.getStatus());

        return responseDTO;
    }

    @Override
    public List<GenericDataDTO> itemStatusToGenericData(List<ItemStatus> itemStatuses) {
        List<GenericDataDTO> genericDataDTOs = new ArrayList<>();
        for (ItemStatus itemStatus : itemStatuses) {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            genericDataDTO.setId(itemStatus.getId());
            genericDataDTO.setName(itemStatus.getStatus());
            genericDataDTOs.add(genericDataDTO);
        }
        return genericDataDTOs;
    }
}
