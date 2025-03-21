package radiant.sispa.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "purchase_order")
public class PurchaseOrder implements Serializable {
    @Id
    @GeneratedValue(generator = "system-uuid")
    private Long id;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;

    @NotNull
    @Column(name = "created_by", updatable = false, nullable = false)
    private String createdBy;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "deleted_at")
    private Date deletedAt;

    @Column(name = "deleted_by")
    private String deletedBy;

    @NotNull
    @Column(name = "company_name", nullable = false)
    private String companyName;

    @NotNull
    @Column(name = "company_address", nullable = false)
    private String companyAddress;

    @NotNull
    @Column(name = "receiver", nullable = false)
    private String receiver;

    @NotNull
    @Column(name = "no_po", nullable = false, unique = true)
    private String noPo;

    @NotNull
    @Column(name = "date_created", nullable = false)
    private String dateCreated;

    @ManyToMany
    @JoinTable(name = "purchase_order_purchase_order_item", joinColumns = @JoinColumn(name = "id_purchase_order"),
            inverseJoinColumns = @JoinColumn(name = "id_purchase_order_item"))
    private List<PurchaseOrderItem> items;

    @NotNull
    @Column(name = "total", nullable = false)
    private Long total;

    @NotNull
    @Column(name = "spelled_out", nullable = false)
    private String spelledOut;

    @NotNull
    @Column(name = "terms", nullable = false)
    private String terms;

    @NotNull
    @Column(name = "place_signed", nullable = false)
    private String placeSigned;

    @NotNull
    @Column(name = "date_signed", nullable = false)
    private String dateSigned;

    @NotNull
    @Column(name = "signee", nullable = false)
    private String signee;

    @NotNull
    @Column(name = "fileName", nullable = false)
    private String fileName;
}

