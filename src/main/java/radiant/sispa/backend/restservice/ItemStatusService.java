package radiant.sispa.backend.restservice;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import radiant.sispa.backend.model.ItemStatus;
import radiant.sispa.backend.model.WorkExperienceCategory;
import radiant.sispa.backend.restdto.request.CreateItemStatusRequestDTO;
import radiant.sispa.backend.restdto.request.CreateWorkExperienceCategoryRequestDTO;
import radiant.sispa.backend.restdto.response.CreateItemStatusResponseDTO;
import radiant.sispa.backend.restdto.response.CreateWorkExperienceCategoryResponseDTO;
import radiant.sispa.backend.restdto.response.GenericDataDTO;

import java.util.List;

public interface ItemStatusService {
    List<ItemStatus> getAllItemStatuses();
    ItemStatus getItemStatusByName(String name) throws EntityNotFoundException;
    CreateItemStatusResponseDTO addItemStatus(CreateItemStatusRequestDTO name, String authHeader) throws EntityExistsException;
    List<GenericDataDTO> itemStatusToGenericData(List<ItemStatus> itemStatuses);
}
