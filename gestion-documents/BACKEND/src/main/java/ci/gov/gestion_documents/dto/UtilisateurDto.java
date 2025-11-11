package ci.gov.gestion_documents.dto;

import ci.gov.gestion_documents.model.Role;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UtilisateurDto {

    private UUID id;
    private String nom;
    private String prenom;
    private String email;
    private Role role;
}
