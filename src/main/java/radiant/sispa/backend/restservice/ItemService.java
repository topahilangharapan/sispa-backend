package radiant.sispa.backend.restservice;

import java.util.List;

import org.springframework.http.ResponseEntity;
import radiant.sispa.backend.restdto.request.CreateItemRequestDTO;
import radiant.sispa.backend.restdto.request.CreatePurchaseOrderRequestDTO;
import radiant.sispa.backend.restdto.response.CreateItemResponseDTO;
import radiant.sispa.backend.restdto.response.CreatePurchaseOrderResponseDTO;
import radiant.sispa.backend.restdto.response.PurchaseOrderResponseDTO;

public interface ItemService {
CreateItemResponseDTO createItem(CreateItemRequestDTO createItemRequestDTO, String authHeader);
}
