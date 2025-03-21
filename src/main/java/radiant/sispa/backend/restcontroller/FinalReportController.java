package radiant.sispa.backend.restcontroller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import radiant.sispa.backend.model.Image;
import radiant.sispa.backend.restdto.request.CreateFinalReportRequestDTO;
import radiant.sispa.backend.restdto.response.*;
import radiant.sispa.backend.restservice.FinalReportService;
import radiant.sispa.backend.repository.ImageDb;

import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/final-report")
public class FinalReportController {

    @Autowired
    private FinalReportService finalReportService;

    @Autowired
    private ImageDb imageDb;

    @PostMapping("/create")
    public ResponseEntity<BaseResponseDTO<CreateFinalReportResponseDTO>> createPdfReport(
            @Valid
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam("data") String data,
            @RequestPart("images") List<MultipartFile> images
    ) {
        BaseResponseDTO<CreateFinalReportResponseDTO> responseDTO = new BaseResponseDTO<>();

        try {
            CreateFinalReportRequestDTO createFinalReportRequestDTO = finalReportService.convertToCreateFinalReportRequestDTO(data, images, authHeader);
            CreateFinalReportResponseDTO pdfReport = finalReportService.generatePdfReport(createFinalReportRequestDTO, authHeader);

            responseDTO.setStatus(HttpStatus.OK.value());
            responseDTO.setData(pdfReport);
            responseDTO.setTimestamp(new Date());
            responseDTO.setMessage("Final Report generated!");
            return ResponseEntity.ok(responseDTO);

        } catch (Exception e) {
            responseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDTO.setTimestamp(new Date());
            responseDTO.setMessage("Failed to generate Final Report: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<BaseResponseDTO<List<FinalReportResponseDTO>>> getAllFinalReports() {
        BaseResponseDTO<List<FinalReportResponseDTO>> responseDTO = new BaseResponseDTO<>();

        try {
            List<FinalReportResponseDTO> allReports = finalReportService.getAllFinalReports();
            responseDTO.setStatus(HttpStatus.OK.value());
            responseDTO.setData(allReports);
            responseDTO.setTimestamp(new Date());
            responseDTO.setMessage("List of final reports retrieved!");
            return ResponseEntity.ok(responseDTO);

        } catch (Exception e) {
            responseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDTO.setTimestamp(new Date());
            responseDTO.setMessage("Failed to retrieve final reports!");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponseDTO<FinalReportResponseDTO>> getFinalReportDetail(@PathVariable("id") Long id) {
        BaseResponseDTO<FinalReportResponseDTO> responseDTO = new BaseResponseDTO<>();

        try {
            FinalReportResponseDTO report = finalReportService.getReportsById(id);

            List<ImageResponseDTO> imageResponses = report.getImages().stream()
                    .map(image -> new ImageResponseDTO(
                            image.getId(),
                            image.getFileName(),
                            image.getFileData()
                    ))
                    .collect(Collectors.toList());

            report.setImages(imageResponses);

            responseDTO.setStatus(HttpStatus.OK.value());
            responseDTO.setData(report);
            responseDTO.setTimestamp(new Date());
            responseDTO.setMessage("Final report retrieved!");
            return ResponseEntity.ok(responseDTO);

        } catch (NoSuchElementException e) {
            responseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            responseDTO.setTimestamp(new Date());
            responseDTO.setMessage("Final report not found!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO);
        } catch (Exception e) {
            responseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDTO.setTimestamp(new Date());
            responseDTO.setMessage("Failed to retrieve final report detail!");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
        }
    }

    @PutMapping("/{id}/delete")
    public ResponseEntity<BaseResponseDTO<Void>> deleteFinalReport(@PathVariable("id") Long id) {
        BaseResponseDTO<Void> responseDTO = new BaseResponseDTO<>();

        try {
            finalReportService.deleteFinalReport(id);
            responseDTO.setStatus(HttpStatus.OK.value());
            responseDTO.setMessage("Final report successfully deleted!");
            responseDTO.setTimestamp(new Date());
            return ResponseEntity.ok(responseDTO);

        } catch (ConstraintViolationException e) {
            responseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            responseDTO.setMessage(e.getMessage());
            responseDTO.setTimestamp(new Date());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
        } catch (EntityNotFoundException e) {
            responseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            responseDTO.setMessage(e.getMessage());
            responseDTO.setTimestamp(new Date());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO);
        }
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadFinalReport(@PathVariable("id") Long id, @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            CreateFinalReportResponseDTO responseDTO = finalReportService.generatePdfById(id, authHeader);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + responseDTO.getFileName() + ".pdf")
                    .body(responseDTO.getPdf());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}