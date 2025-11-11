package ci.gov.gestion_documents.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class AuditableEntity {

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "deleted", nullable = false)
    private boolean deleted;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "deleted_by")
    private String deletedBy;

    @Column(name = "actif", nullable = false)
    private boolean actif;

    @Column(name = "activate_at")
    private Instant activateAt;

    @Column(name = "activate_by")
    private String activateBy;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        if (!deleted) {
            deleted = false;
        }
        if (!actif) {
            actif = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
