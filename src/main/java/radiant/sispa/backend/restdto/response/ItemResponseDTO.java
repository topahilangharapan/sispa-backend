package radiant.sispa.backend.restdto.response;
import java.time.Instant;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;


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
    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "Asia/Jakarta")
    private Instant createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "Asia/Jakarta")
    private Instant updatedAt;

    private String createdBy;
    private String updatedBy;

    @JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "Asia/Jakarta")
    private Instant deletedAt;

}
