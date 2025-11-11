package ci.gov.gestion_documents.config;

import ci.gov.gestion_documents.domaine.Utilisateur;
import ci.gov.gestion_documents.model.Role;
import ci.gov.gestion_documents.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DonneeInitiale implements CommandLineRunner {
    @Autowired
    private UtilisateurRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.findByEmail("admin@gouv.ci").isEmpty()) {
            Utilisateur admin = new Utilisateur();
            admin.setEmail("admin@gouv.ci");
            admin.setNom("Administrateur");
            admin.setPrenom("Système");
            admin.setMotDePasse(passwordEncoder.encode("admin123@"));
            admin.setRole(Role.ADMIN);
            admin.setActif(true);

            userRepository.save(admin);
            System.out.println("🧑‍💼 Admin créé : admin@gouv.ci / admin123@");
        }
    }
}
