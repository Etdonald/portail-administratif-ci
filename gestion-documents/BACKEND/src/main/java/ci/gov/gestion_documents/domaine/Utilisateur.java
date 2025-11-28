package ci.gov.gestion_documents.domaine;

import ci.gov.gestion_documents.model.AuditableEntity;
import ci.gov.gestion_documents.model.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "utilisateur")
public class Utilisateur extends AuditableEntity {
    @Id
    @GeneratedValue
    private UUID id;
    private String nom;
    private String prenom;
    @Column(unique = true)
    private String email;
    private String motDePasse;
    private boolean actif;
    private boolean emailVerifie = false;

    @Enumerated(EnumType.STRING)
    private Role role;
}