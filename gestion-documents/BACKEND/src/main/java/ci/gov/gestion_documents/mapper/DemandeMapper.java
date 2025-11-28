package ci.gov.gestion_documents.mapper;

import ci.gov.gestion_documents.dto.DemandeDto;
import ci.gov.gestion_documents.domaine.Demande;
import ci.gov.gestion_documents.model.StatutDemande;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface DemandeMapper {

    DemandeDto toDto(Demande demande);

    Demande toEntity(DemandeDto dto);

    @Mapping(target = "utilisateur", ignore = true) // Géré manuellement dans le service
    @Mapping(target = "dateEmission", source = "dateEmission", dateFormat = "dd/MM/yyyy")
    @Mapping(target = "dateExpiration", source = "dateExpiration", dateFormat = "dd/MM/yyyy")
    Demande toEntityWithCustomMapping(DemandeDto dto);
}
