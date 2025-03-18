package radiant.sispa.backend.restdto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreatePurchaseOrderRequestDTO {
    private String companyName;

    private String companyAddress;

    private String receiver;

    private List<Map<String, String>> items;

    private String terms;

    private String placeSigned;

    private String dateCreated;

    private String dateSigned;

    private String signee;
}
