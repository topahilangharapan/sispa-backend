package radiant.sispa.backend.restservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.web.multipart.MultipartFile;
import radiant.sispa.backend.restdto.request.AddClientRequestRestDTO;
import radiant.sispa.backend.restdto.request.CreateFinalReportRequestDTO;
import radiant.sispa.backend.restdto.request.CreateInvoiceRequestDTO;
import radiant.sispa.backend.restdto.request.UpdateClientRequestRestDTO;
import radiant.sispa.backend.restdto.response.*;
import radiant.sispa.backend.restdto.response.*;

import java.io.IOException;
import java.util.List;

public interface FinalReportService {
    CreateFinalReportResponseDTO generatePdfReport(CreateFinalReportRequestDTO createFinalReportRequestDTO, String authHeader);
    CreateFinalReportRequestDTO convertToCreateFinalReportRequestDTO(String data, List<MultipartFile> images, String createdBy) throws IOException;
    List<FinalReportResponseDTO> getAllFinalReports();
    FinalReportResponseDTO getReportsById(Long id);
    void deleteFinalReport(Long id) throws EntityNotFoundException;
    byte[] getPdfFile(Long id);
}
