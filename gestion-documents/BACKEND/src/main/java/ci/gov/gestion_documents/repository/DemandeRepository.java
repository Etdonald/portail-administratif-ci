package ci.gov.gestion_documents.repository;

import ci.gov.gestion_documents.domaine.Demande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DemandeRepository extends JpaRepository<Demande, UUID> {
}
