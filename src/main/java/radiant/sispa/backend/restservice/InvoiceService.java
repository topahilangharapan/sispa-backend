package radiant.sispa.backend.restservice;

import org.springframework.web.multipart.MultipartFile;
import radiant.sispa.backend.model.PurchaseOrder;
import radiant.sispa.backend.restdto.request.CreateInvoiceRequestDTO;
import radiant.sispa.backend.restdto.request.CreatePurchaseOrderRequestDTO;
import radiant.sispa.backend.restdto.response.CreateInvoiceResponseDTO;
import radiant.sispa.backend.restdto.response.CreatePurchaseOrderResponseDTO;
import radiant.sispa.backend.restdto.response.PurchaseOrderResponseDTO;

import java.util.List;

public interface InvoiceService {
    CreateInvoiceResponseDTO generatePdfReport(CreateInvoiceRequestDTO createInvoiceRequestDTO, String authHeader);
}
