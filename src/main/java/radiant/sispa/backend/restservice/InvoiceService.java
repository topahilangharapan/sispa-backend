package radiant.sispa.backend.restservice;

import org.springframework.web.multipart.MultipartFile;
import radiant.sispa.backend.model.PurchaseOrder;
import radiant.sispa.backend.restdto.request.CreateInvoiceRequestDTO;
import radiant.sispa.backend.restdto.response.CreateInvoiceResponseDTO;
import radiant.sispa.backend.restdto.response.InvoiceResponseDTO;

import java.util.List;

public interface InvoiceService {
    CreateInvoiceResponseDTO generatePdfReport(CreateInvoiceRequestDTO createInvoiceRequestDTO, String authHeader);
    List<InvoiceResponseDTO> getAllInvoices();
    InvoiceResponseDTO getInvoiceById(Long id);
    void deleteInvoice(Long id);
    CreateInvoiceResponseDTO generatePdfByInvoiceId(Long invoiceId, String authHeader);
}
