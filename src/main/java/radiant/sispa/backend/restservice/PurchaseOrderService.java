package radiant.sispa.backend.restservice;

import org.springframework.http.ResponseEntity;
import radiant.sispa.backend.restdto.request.CreatePurchaseOrderRequestDTO;
import radiant.sispa.backend.restdto.response.CreatePurchaseOrderResponseDTO;

public interface PurchaseOrderService {
    CreatePurchaseOrderResponseDTO generatePdfReport(CreatePurchaseOrderRequestDTO createPurchaseOrderRequestDTO, String authHeader);
}
