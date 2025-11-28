package ci.gov.gestion_documents.repository;

import ci.gov.gestion_documents.domaine.Demande;
import ci.gov.gestion_documents.model.StatutDemande;
import ci.gov.gestion_documents.model.TypeDemande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DemandeRepository extends JpaRepository<Demande, UUID> {
    List<Demande> findByTypeDemande(TypeDemande typeDemande);

    List<Demande> findByUtilisateurEmail(String email);

    List<Demande> findByUtilisateurEmailAndTypeDemande(String email, TypeDemande typeDemande);

    List<Demande> findByStatut(StatutDemande statut);

    List<Demande> findByTypeDemandeAndStatut(TypeDemande typeDemande, StatutDemande statut);
}
