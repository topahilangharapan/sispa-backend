package radiant.sispa.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@MappedSuperclass
public abstract class Transaction implements Serializable {
    @Id
    protected String id;

    @NotNull
    @Column(name = "amount", nullable = false)
    protected Double amount;

    @Column(name = "description")
    protected String description;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    protected Instant createdAt;

    @NotNull
    @Column(name = "created_by", updatable = false, nullable = false)
    protected String createdBy;

    @UpdateTimestamp
    @Column(name = "updated_at")
    protected Instant updatedAt;

    @Column(name = "updated_by")
    protected String updatedBy;

    @Column(name = "deleted_at")
    protected Instant deletedAt;

    @Column(name = "deleted_by")
    protected String deletedBy;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_account", referencedColumnName = "id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    protected Account account;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_category", referencedColumnName = "id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    protected TransactionCategory category;
}
