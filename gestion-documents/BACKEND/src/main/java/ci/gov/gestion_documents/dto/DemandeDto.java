package ci.gov.gestion_documents.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DemandeDto {
    private UUID id;
    private String typeDemande;
    private String statut;
    private String utilisateurId;
    private LocalDateTime dateSoumission;
}
