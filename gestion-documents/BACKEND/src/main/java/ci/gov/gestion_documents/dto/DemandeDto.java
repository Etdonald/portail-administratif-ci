package ci.gov.gestion_documents.dto;

import ci.gov.gestion_documents.model.StatutDemande;
import ci.gov.gestion_documents.model.TypeDemande;
import ci.gov.gestion_documents.model.TypeDemandeCni;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DemandeDto {
    // Champs de base
    private UUID id;
    private TypeDemande typeDemande;
    private TypeDemandeCni typeDemandeCni;
    private StatutDemande statut;
    private UUID utilisateurId;
    private boolean traitee;
    private String commentaireAdmin;
    private LocalDateTime dateCreation;
    private LocalDateTime dateSoumission;

    // Champs communs à toutes les demandes
    private String dateNaissance;
    private String lieuNaissance;

    // Champs spécifiques CNI (null pour les autres types)
    private String nom;
    private String prenoms;
    private String profession;
    private String sexe;
    private String nationalite;
    private String taille;

    private String adresse;
    private String ville;
    private String region;
    private String telephone;
    private String email;

    private String numeroCNI;
    private String numeroNNI;
    private String dateEmission;
    private String dateExpiration;
    private String lieuEmission;
    private String autoriteEmettrice;

    private String photoIdentitePath;
    private String ancienneCniPath;

    private String cheminCniPdf;
}