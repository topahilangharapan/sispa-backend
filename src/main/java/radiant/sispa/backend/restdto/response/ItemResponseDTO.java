package radiant.sispa.backend.restdto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import radiant.sispa.backend.model.Category;
import radiant.sispa.backend.model.ItemStatus;
import radiant.sispa.backend.model.PurchaseOrderItem;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemResponseDTO {
    private Long id;
    private String title;
    private String unit;
    private Long pricePerUnit;
    private String description;
    private String category;
    private String status;
}
