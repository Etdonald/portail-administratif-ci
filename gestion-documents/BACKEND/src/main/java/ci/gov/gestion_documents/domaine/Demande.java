package ci.gov.gestion_documents.domaine;

import ci.gov.gestion_documents.model.AuditableEntity;
import ci.gov.gestion_documents.model.StatutDemande;
import ci.gov.gestion_documents.model.TypeDemande;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "demande")
public class Demande extends AuditableEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    private TypeDemande typeDemande;

    @Enumerated(EnumType.STRING)
    private StatutDemande statut;

    private LocalDateTime dateCreation;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    private LocalDateTime dateSoumission = LocalDateTime.now();

}
