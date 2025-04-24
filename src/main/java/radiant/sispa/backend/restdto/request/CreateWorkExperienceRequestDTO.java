package radiant.sispa.backend.restdto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import radiant.sispa.backend.model.WorkExperienceCategory;

import java.time.LocalDate;

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
