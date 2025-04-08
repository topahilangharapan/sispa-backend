package radiant.sispa.backend.restservice;

import java.util.List;

import org.springframework.http.ResponseEntity;
import radiant.sispa.backend.restdto.request.CreatePurchaseOrderRequestDTO;
import radiant.sispa.backend.restdto.response.CreatePurchaseOrderResponseDTO;
import radiant.sispa.backend.restdto.response.PurchaseOrderResponseDTO;

public interface PurchaseOrderService {
    CreatePurchaseOrderResponseDTO generatePdfReport(CreatePurchaseOrderRequestDTO createPurchaseOrderRequestDTO, String authHeader);
    List<PurchaseOrderResponseDTO> getAllPurchaseOrders();
    PurchaseOrderResponseDTO getPurchaseOrderById(Long id);
    void deletePurchaseOrder(Long id);
    CreatePurchaseOrderResponseDTO generatePdfByPurchaseOrderId(Long purchaseOrderId, String authHeader);
}
