package radiant.sispa.backend.restdto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateInvoiceRequestDTO {
    private Long purchaseOrderId;

    private String receiver;

    private String datePaid;

    private String ppnPercentage;

    private String bankName;

    private String accountNumber;

    private String onBehalf;

    private String placeSigned;

    private String dateCreated;

    private String dateSigned;

    private String signee;

    private String event;
}
