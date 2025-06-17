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
import radiant.sispa.backend.restdto.request.CreateInvoiceRequestDTO;
import radiant.sispa.backend.restdto.response.BaseResponseDTO;
import radiant.sispa.backend.restdto.response.CreateInvoiceResponseDTO;
import radiant.sispa.backend.restdto.response.InvoiceResponseDTO;
import radiant.sispa.backend.restservice.InvoiceService;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;


@RestController
@RequestMapping("/api/invoice")
public class InvoiceController {
    @Autowired
    private InvoiceService invoiceService;

    @PostMapping("/test/create")
    public ResponseEntity<byte[]> createPdfReportTest(
            @Valid
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody CreateInvoiceRequestDTO createInvoiceRequestDTO,
            BindingResult bindingResult) {

        byte[] pdfBytes = invoiceService.generatePdfReport(createInvoiceRequestDTO, authHeader).getPdf();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=invoice.pdf") // Menampilkan langsung di Postman
                .body(pdfBytes);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPdfReport(
            @Valid
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody CreateInvoiceRequestDTO createInvoiceRequestDTO,
            BindingResult bindingResult) {

        var baseResponseDTO = new BaseResponseDTO<CreateInvoiceResponseDTO>();

        if (bindingResult.hasFieldErrors()) {
            String errorMessages = "";
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMessages += error.getDefaultMessage() + "; ";
            }

            baseResponseDTO.setStatus(HttpStatus.BAD_REQUEST.value());
            baseResponseDTO.setMessage(errorMessages);
            baseResponseDTO.setTimestamp(new Date());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.BAD_REQUEST);
        }

        try {
            CreateInvoiceResponseDTO pdfReport = invoiceService.generatePdfReport(createInvoiceRequestDTO, authHeader);
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(pdfReport);
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage(String.format("Invoice generated!"));
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage(String.format("Failed to generate invoice: %s !", e.getMessage()));
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllInvoice() {
        var baseResponseDTO = new BaseResponseDTO<List<InvoiceResponseDTO>>();
        try {
            List<InvoiceResponseDTO> allOrders = invoiceService.getAllInvoices();

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(allOrders);
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage("List of invoices retrieved!");
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage("Failed to retrieve invoices!");
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getInvoiceDetail(@PathVariable("id") Long id) {
        var baseResponseDTO = new BaseResponseDTO<InvoiceResponseDTO>();
        try {
            InvoiceResponseDTO invoice = invoiceService.getInvoiceById(id);

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(invoice);
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage("Invoice retrieved!");
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage(e.getMessage());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage("Failed to retrieve invoice detail!");
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteInvoice(@PathVariable("id") Long id) {
        var baseResponseDTO = new BaseResponseDTO<String>();
        try {
            invoiceService.deleteInvoice(id);

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData("Deleted");
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage("Invoice deleted successfully!");
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage(e.getMessage());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage("Failed to delete Invoice!");
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    @GetMapping("/{id}/download")
//    public ResponseEntity<byte[]> downloadInvoicePdf(@PathVariable("id") Long id, @RequestHeader(value = "Authorization", required = false) String authHeader) {
//        try {
//            CreateInvoiceRequestDTO requestDTO = new CreateInvoiceRequestDTO();
//            requestDTO.setPurchaseOrderId(id);
//
//            CreateInvoiceResponseDTO responseDTO = invoiceService.generatePdfReport(requestDTO, authHeader);
//
//            return ResponseEntity.ok()
//                    .contentType(MediaType.APPLICATION_PDF)
//                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + responseDTO.getFileName())
//                    .body(responseDTO.getPdf());
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        }
//    }
    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable("id") Long id, @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            CreateInvoiceResponseDTO responseDTO = invoiceService.generatePdfByInvoiceId(id, authHeader);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + responseDTO.getFileName() + ".pdf")
                    .body(responseDTO.getPdf());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
