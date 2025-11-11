package ci.gov.gestion_documents.mapper;

import ci.gov.gestion_documents.domaine.Utilisateur;
import ci.gov.gestion_documents.dto.UtilisateurDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UtilisateurMapper {
    UtilisateurMapper INSTANCE = Mappers.getMapper(UtilisateurMapper.class);

    UtilisateurDto toDto(Utilisateur utilisateur);
    Utilisateur toEntity(UtilisateurDto dto);
}
