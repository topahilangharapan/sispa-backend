package radiant.sispa.backend.restservice;

import radiant.sispa.backend.restdto.request.CreateInvoiceRequestDTO;
import radiant.sispa.backend.restdto.response.CreateInvoiceResponseDTO;
import radiant.sispa.backend.restdto.response.InvoiceResponseDTO;

import java.util.List;

public interface InvoiceService {
    CreateInvoiceResponseDTO generatePdfReport(CreateInvoiceRequestDTO createInvoiceRequestDTO, String authHeader);
    List<InvoiceResponseDTO> getAllInvoices();
    InvoiceResponseDTO getInvoiceById(Long id);
    void deleteInvoice(Long id);
}
