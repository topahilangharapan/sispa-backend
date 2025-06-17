package radiant.sispa.backend.restdto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateWorkExperienceRequestDTO {
    private Long freelancerId;
    private String category;
    private String title;
    private String description;
    private Boolean isStillWorking;
    private String startDate;
    private String endDate;
}
