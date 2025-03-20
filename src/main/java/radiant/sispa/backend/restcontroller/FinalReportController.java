package radiant.sispa.backend.restcontroller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import radiant.sispa.backend.model.Image;
import radiant.sispa.backend.repository.ImageDb;
import radiant.sispa.backend.restdto.request.CreateFinalReportRequestDTO;
import radiant.sispa.backend.restdto.request.CreateInvoiceRequestDTO;
import radiant.sispa.backend.restdto.response.*;
import radiant.sispa.backend.restservice.FinalReportService;
import radiant.sispa.backend.restservice.InvoiceService;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;


@RestController
@RequestMapping("/api/final-report")
public class FinalReportController {
    @Autowired
    private FinalReportService finalReportService;

    @Autowired
    private ImageDb imageDb;


    @PostMapping("/test/create")
    public ResponseEntity<byte[]> createPdfReportTest(
            @Valid
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam("data") String data,
            @RequestPart("images") List<MultipartFile> images) throws IOException {

        CreateFinalReportRequestDTO createFinalReportRequestDTO = finalReportService.convertToCreateFinalReportRequestDTO(data, images, authHeader);
        byte[] pdfBytes = finalReportService.generatePdfReport(createFinalReportRequestDTO, authHeader).getPdf();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=final_report.pdf") // Menampilkan langsung di Postman
                .body(pdfBytes);
    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<byte[]> getImage(@PathVariable Long id) throws Exception {
//        Image image = imageDb.findByIdAndDeletedAtNull(id);
//        if (image != null) {
//            return ResponseEntity.ok()
//                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + image.getFileName() + "\"")
//                    .contentType(MediaType.IMAGE_JPEG) // Atur tipe konten sesuai format gambar
//                    .body(image.getFileData());
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }

    @PostMapping("/create")
    public ResponseEntity<?> createPdfReport(
            @Valid
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam("data") String data,
            @RequestPart("images") List<MultipartFile> images
            ) {

        var baseResponseDTO = new BaseResponseDTO<CreateFinalReportResponseDTO>();

        try {
            CreateFinalReportRequestDTO createFinalReportRequestDTO = finalReportService.convertToCreateFinalReportRequestDTO(data, images, authHeader);
            CreateFinalReportResponseDTO pdfReport = finalReportService.generatePdfReport(createFinalReportRequestDTO, authHeader);
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(pdfReport);
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage(String.format("Final Report generated!"));
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage(String.format("Failed to generate Final Report: %s !", e.getMessage()));
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllFinalReports() {
        var baseResponseDTO = new BaseResponseDTO<List<FinalReportResponseDTO>>();
        try {
            List<FinalReportResponseDTO> allOrders = finalReportService.getAllFinalReports();

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(allOrders);
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage("List of final report retrieved!");
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage("Failed to retrieve final reports!");
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFinalReportDetail(@PathVariable("id") Long id) {
        var baseResponseDTO = new BaseResponseDTO<FinalReportResponseDTO>();
        try {
            FinalReportResponseDTO report = finalReportService.getReportsById(id);

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(report);
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage("Final report retrieved!");
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage(e.getMessage());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage("Failed to retrieve final report detail!");
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
