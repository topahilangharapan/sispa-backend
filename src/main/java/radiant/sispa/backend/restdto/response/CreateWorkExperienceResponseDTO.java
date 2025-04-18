package radiant.sispa.backend.restdto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateWorkExperienceResponseDTO {
    private String category;
    private String title;
    private String description;
    private Boolean isStillWorking;
    private String startDate;
    private String endDate;
}
