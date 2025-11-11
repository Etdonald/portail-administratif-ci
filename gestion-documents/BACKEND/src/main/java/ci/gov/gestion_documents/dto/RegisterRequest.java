package ci.gov.gestion_documents.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
}
