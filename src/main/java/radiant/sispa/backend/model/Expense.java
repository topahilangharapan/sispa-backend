package radiant.sispa.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "expense")
public class Expense extends Transaction {

    @Column(name = "is_admin")
    private boolean isAdmin;
}
