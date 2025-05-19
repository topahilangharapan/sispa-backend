package radiant.sispa.backend.restdto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AccountResponseDTO {
    private Long id;
    private String name;
    private String no;
    private String bank;
    private double balance;
    private double adminFee;
    private double interestRate;

    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;
    private String updatedBy;
    private Instant deletedAt;
    private String deletedBy;
}
