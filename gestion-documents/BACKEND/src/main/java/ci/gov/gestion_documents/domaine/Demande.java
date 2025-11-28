package ci.gov.gestion_documents.domaine;

import ci.gov.gestion_documents.model.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
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

    private String dateNaissance;
    private String lieuNaissance;

    @Enumerated(EnumType.STRING)
    private StatutDemande statut = StatutDemande.EN_ATTENTE;

    private boolean traitee = false;
    private String commentaireAdmin;

    private LocalDateTime dateCreation = LocalDateTime.now();
    private LocalDateTime dateSoumission = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    @Column(name = "chemin_cni_pdf")
    private String cheminCniPdf;

    // Champs spécifiques CNI
    @Enumerated(EnumType.STRING)
    private TypeDemandeCni typeDemandeCni;

    private String nom;
    private String prenoms;
    private String profession;
    private String sexe;
    private String nationalite = "Ivoirienne";
    private String taille;

    private String adresse;
    private String ville;
    private String region;
    private String telephone;
    private String email;

    // Informations anciennes pièces
    private String numeroCNI;
    private String numeroNNI;
    private LocalDate dateEmission;
    private LocalDate dateExpiration;
    private String lieuEmission = "ABIDJAN";
    private String autoriteEmettrice = "DIRECTION GÉNÉRALE DE LA POLICE NATIONALE";

    @Column(name = "photo_identite_path")
    private String photoIdentitePath;

    @Column(name = "ancienne_cni_path")
    private String ancienneCniPath;

    // Fabrique dynamique pour les CNI
    public static Demande createDemandeCni(Utilisateur utilisateur) {
        return Demande.builder()
                .typeDemande(TypeDemande.CNI)
                .statut(StatutDemande.EN_ATTENTE)
                .traitee(false)
                .dateCreation(LocalDateTime.now())
                .dateSoumission(LocalDateTime.now())
                .nationalite("Ivoirienne")
                .lieuEmission("ABIDJAN")
                .autoriteEmettrice("DIRECTION GÉNÉRALE DE LA POLICE NATIONALE")
                .utilisateur(utilisateur)
                .build();
    }
}
