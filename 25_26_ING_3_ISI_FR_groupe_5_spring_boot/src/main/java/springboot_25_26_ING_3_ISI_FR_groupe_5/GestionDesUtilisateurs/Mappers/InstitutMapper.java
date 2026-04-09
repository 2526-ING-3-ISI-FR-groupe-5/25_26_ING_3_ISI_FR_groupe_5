package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Institut;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.institut.InstitutRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.institut.InstitutResponse;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InstitutMapper {

    @Mapping(target = "nombreEcoles", source = "ecoles", qualifiedByName = "countEcoles")
    @Mapping(target = "nombreUtilisateurs", source = "utilisateurs", qualifiedByName = "countUtilisateurs")
    InstitutResponse toResponse(Institut institut);

    List<InstitutResponse> toResponseList(List<Institut> instituts);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ecoles", ignore = true)
    @Mapping(target = "utilisateurs", ignore = true)
    @Mapping(target = "administrateur", ignore = true)
    Institut toEntity(InstitutRequest request);

    @Named("countEcoles")
    default int countEcoles(java.util.Collection<?> ecoles) {
        return ecoles != null ? ecoles.size() : 0;
    }

    @Named("countUtilisateurs")
    default int countUtilisateurs(java.util.Collection<?> utilisateurs) {
        return utilisateurs != null ? utilisateurs.size() : 0;
    }
}