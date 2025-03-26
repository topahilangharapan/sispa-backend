package radiant.sispa.backend.restdto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderResponseDTO {
    private Long id;
    private String companyName;
    private String companyAddress;
    private String receiver;
    private String noPo;
    private String dateCreated;
    private Long total;
    private String spelledOut;
    private String terms;
    private String placeSigned;
    private String dateSigned;
    private String signee;
    private String deletedAt;

    private List<PurchaseOrderItemResponseDTO> items;
}