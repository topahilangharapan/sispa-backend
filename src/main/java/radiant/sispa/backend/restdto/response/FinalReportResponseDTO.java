package radiant.sispa.backend.restdto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import radiant.sispa.backend.model.Image;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinalReportResponseDTO {
    private Long id;
    private Instant createdAt;
    private String createdBy;
    private String fileName;
    private List<ImageResponseDTO> images;
    private String company;
    private String event;
    private String eventDate;
}
