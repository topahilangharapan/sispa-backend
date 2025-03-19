package radiant.sispa.backend.restdto.response;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import radiant.sispa.backend.model.PurchaseOrder;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceResponseDTO {
    private Long id;
    private Instant createdAt;
    private String createdBy;
    private String receiver;
    private String dateCreated;
    private String noInvoice;
    private String noPo;
    private String datePaid;
    private Long subTotal;
    private Double ppnPercentage;
    private Long ppn;
    private Long total;
    private String spelledOut;
    private String bankName;
    private String accountNumber;
    private String onBehalf;
    private String placeSigned;
    private String dateSigned;
    private String signee;
    private String event;
    private String fileName;

    private List<PurchaseOrderItemResponseDTO> items;
}
