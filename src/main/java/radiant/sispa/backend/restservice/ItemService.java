package radiant.sispa.backend.restservice;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;
import radiant.sispa.backend.model.Item;
import radiant.sispa.backend.restdto.request.CreateItemRequestDTO;
import radiant.sispa.backend.restdto.request.UpdateItemRequestRestDTO;
import radiant.sispa.backend.restdto.request.UpdateItemStatusRequestRestDTO;
import radiant.sispa.backend.restdto.response.CreateItemResponseDTO;
import radiant.sispa.backend.restdto.response.ItemResponseDTO;

public interface ItemService {
    CreateItemResponseDTO createItem(CreateItemRequestDTO createItemRequestDTO, String authHeader);
    Item getItemById(Long id);
    ItemResponseDTO detailItem(Long id);
    ItemResponseDTO updateItem(Long id, UpdateItemRequestRestDTO itemDTO, String username);
    List<ItemResponseDTO> getAllItems();
    void deleteItem(Long id) throws EntityNotFoundException;
    ItemResponseDTO updateItemStatus(Long id, Long statusId, UpdateItemStatusRequestRestDTO itemDTO, String username);
}
