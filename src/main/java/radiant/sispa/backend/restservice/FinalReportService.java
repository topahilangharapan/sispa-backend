package radiant.sispa.backend.restservice;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.web.multipart.MultipartFile;
import radiant.sispa.backend.restdto.request.AddClientRequestRestDTO;
import radiant.sispa.backend.restdto.request.CreateFinalReportRequestDTO;
import radiant.sispa.backend.restdto.request.CreateInvoiceRequestDTO;
import radiant.sispa.backend.restdto.request.UpdateClientRequestRestDTO;
import radiant.sispa.backend.restdto.response.ClientResponseDTO;
import radiant.sispa.backend.restdto.response.CreateFinalReportResponseDTO;
import radiant.sispa.backend.restdto.response.CreateInvoiceResponseDTO;

import java.util.List;

public interface FinalReportService {
    CreateFinalReportResponseDTO generatePdfReport(String data, List<MultipartFile> images, String authHeader);
}
