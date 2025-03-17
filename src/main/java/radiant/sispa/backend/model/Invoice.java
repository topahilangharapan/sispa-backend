package radiant.sispa.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "invoice")
public class Invoice implements Serializable {
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
    private Instant deletedAt;

    @Column(name = "deleted_by")
    private String deletedBy;

    @NotNull
    @Column(name = "receiver", nullable = false)
    private String receiver;

    @NotNull
    @Column(name = "date_created", nullable = false)
    private String dateCreated;

    @NotNull
    @Column(name = "no_invoice", nullable = false, unique = true)
    private String noInvoice;

    @NotNull
    @Column(name = "no_po", nullable = false)
    private String noPo;

    @NotNull
    @Column(name = "date_paid", nullable = false)
    private String datePaid;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_purchase_order", referencedColumnName = "id")
    private PurchaseOrder purchaseOrder;

    @NotNull
    @Column(name = "sub_total", nullable = false)
    private Long subTotal;

    @NotNull
    @Column(name = "ppn_pecentage", nullable = false)
    private Double ppnPercentage;

    @NotNull
    @Column(name = "ppn", nullable = false)
    private Long ppn;

    @NotNull
    @Column(name = "total", nullable = false)
    private Long total;

    @NotNull
    @Column(name = "spelled_out", nullable = false)
    private String spelledOut;

    @NotNull
    @Column(name = "bank_name", nullable = false)
    private String bankName;

    @NotNull
    @Column(name = "accountNumber", nullable = false)
    private String accountNumber;

    @NotNull
    @Column(name = "onBehalf", nullable = false)
    private String onBehalf;

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
    @Column(name = "event", nullable = false)
    private String event;

    @NotNull
    @Column(name = "fileName", nullable = false)
    private String fileName;
}

