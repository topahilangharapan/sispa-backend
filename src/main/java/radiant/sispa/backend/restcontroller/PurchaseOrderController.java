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
import radiant.sispa.backend.restdto.request.CreatePurchaseOrderRequestDTO;
import radiant.sispa.backend.restdto.response.BaseResponseDTO;
import radiant.sispa.backend.restdto.response.CreatePurchaseOrderResponseDTO;
import radiant.sispa.backend.restdto.response.PurchaseOrderResponseDTO;
import radiant.sispa.backend.restservice.PurchaseOrderService;

import java.util.*;


@RestController
@RequestMapping("/api/purchase-order")
public class PurchaseOrderController {
    @Autowired
    private PurchaseOrderService purchaseOrderService;

//    @GetMapping("/sample")
//    public ResponseEntity<byte[]> getPdfReport() {
//        return purchaseOrderService.generatePdfReport();
//    }

    @PostMapping("/test/create")
    public ResponseEntity<byte[]> createPdfReportTest(
            @Valid
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody CreatePurchaseOrderRequestDTO createPurchaseOrderRequestDTO,
            BindingResult bindingResult) {

        byte[] pdfBytes = purchaseOrderService.generatePdfReport(createPurchaseOrderRequestDTO, authHeader).getPdf();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=invoice.pdf") // Menampilkan langsung di Postman
                .body(pdfBytes);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPdfReport(
            @Valid
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody CreatePurchaseOrderRequestDTO createPurchaseOrderRequestDTO,
            BindingResult bindingResult) {

        var baseResponseDTO = new BaseResponseDTO<CreatePurchaseOrderResponseDTO>();

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
            CreatePurchaseOrderResponseDTO pdfReport = purchaseOrderService.generatePdfReport(createPurchaseOrderRequestDTO, authHeader);
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(pdfReport);
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage(String.format("Purchase order generated!"));
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);

        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage(String.format("Failed to generate purchase order: %s !", e.getMessage()));
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllPurchaseOrders() {
        var baseResponseDTO = new BaseResponseDTO<List<PurchaseOrderResponseDTO>>();
        try {
            List<PurchaseOrderResponseDTO> allOrders = purchaseOrderService.getAllPurchaseOrders();

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(allOrders);
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage("List of purchase orders retrieved!");
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage("Failed to retrieve purchase orders!");
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPurchaseOrderDetail(@PathVariable("id") Long id) {
        var baseResponseDTO = new BaseResponseDTO<PurchaseOrderResponseDTO>();
        try {
            PurchaseOrderResponseDTO purchaseOrder = purchaseOrderService.getPurchaseOrderById(id);

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(purchaseOrder);
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage("Purchase order retrieved!");
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage(e.getMessage());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage("Failed to retrieve purchase order detail!");
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> softDeletePurchaseOrder(@PathVariable("id") Long id, @RequestHeader("Authorization") String authHeader) {
        var baseResponseDTO = new BaseResponseDTO<String>();
        try {
            purchaseOrderService.deletePurchaseOrder(id);

            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData("Deleted");
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage("Purchase order marked as deleted successfully!");
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage(e.getMessage());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage("Failed to mark purchase order as deleted!");
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<?> downloadPurchaseOrder(@PathVariable("id") Long id, @RequestHeader("Authorization") String authHeader) {
        var baseResponseDTO = new BaseResponseDTO<CreatePurchaseOrderResponseDTO>();
        try {
            // Use the new method to generate PDF from the existing purchase order
            CreatePurchaseOrderResponseDTO pdfReport = purchaseOrderService.generatePdfByPurchaseOrderId(id, authHeader);
            
            baseResponseDTO.setStatus(HttpStatus.OK.value());
            baseResponseDTO.setData(pdfReport);
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage("Purchase order PDF generated successfully!");
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            baseResponseDTO.setStatus(HttpStatus.NOT_FOUND.value());
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage(e.getMessage());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            baseResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            baseResponseDTO.setTimestamp(new Date());
            baseResponseDTO.setMessage("Failed to generate purchase order PDF: " + e.getMessage());
            return new ResponseEntity<>(baseResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    
}
