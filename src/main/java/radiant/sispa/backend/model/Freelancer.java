package radiant.sispa.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "freelancer")
@PrimaryKeyJoinColumn(name = "id")
public class Freelancer extends UserModel {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_education_level", referencedColumnName = "id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private EducationLevel education;

    @OneToMany(mappedBy = "freelancer", fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private List<WorkExperience> workExperiences;

    @Column(name = "reason")
    private String reason;

    @Column(name = "nik", unique = true)
    private String nik;

    @Column(name = "display_nik")
    private String displayNik;

    @Column(name = "is_working")
    private Boolean isWorking;
    
    @Column(name = "approved_at")
    private Instant approvedAt;

    @Column(name = "rejected_at")
    private Instant rejectedAt;
}
