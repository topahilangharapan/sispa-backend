package radiant.sispa.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "income")
public class Income extends Transaction {

    @Column(name = "is_interest")
    private boolean isInterest;
}
