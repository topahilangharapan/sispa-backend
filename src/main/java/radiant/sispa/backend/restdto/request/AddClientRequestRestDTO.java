package radiant.sispa.backend.restdto.request;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AddClientRequestRestDTO {
    private String name;
    private String contact;
    private String email;
    private String address;
    private String industry;
    private String description;
}
