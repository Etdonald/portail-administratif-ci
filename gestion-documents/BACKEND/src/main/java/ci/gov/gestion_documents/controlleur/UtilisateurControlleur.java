package ci.gov.gestion_documents.controlleur;

import ci.gov.gestion_documents.domaine.Utilisateur;
import ci.gov.gestion_documents.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UtilisateurControlleur {

    @Autowired
    private UtilisateurRepository  utilisateurRepository;

    // Endpoint pour récupérer le profil de l'utilisateur connecté
    @GetMapping("/me")
    public Utilisateur getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // L'email est utilisé comme identifiant unique

        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }
}
