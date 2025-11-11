package ci.gov.gestion_documents.mapper;

import ci.gov.gestion_documents.dto.DemandeDto;
import ci.gov.gestion_documents.domaine.Demande;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface DemandeMapper {
    DemandeMapper INSTANCE = Mappers.getMapper(DemandeMapper.class);

    @Mapping(source = "utilisateur.id", target = "utilisateurId")
    DemandeDto toDto(Demande demande);

    @Mapping(source = "utilisateurId", target = "utilisateur.id")
    Demande toEntity(DemandeDto dto);
}
