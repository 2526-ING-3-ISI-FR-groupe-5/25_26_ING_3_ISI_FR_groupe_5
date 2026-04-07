package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.PlageHoraire.PlageHoraireResponse;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.PlageHoraire;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PlageHoraireMapper {

    @Mapping(target = "classeNom", source = "classe.nom")
    @Mapping(target = "ueNom", source = "programmationUE.ue.nom")
    @Mapping(target = "ueCode", source = "programmationUE.ue.code")
    @Mapping(target = "enseignantsNoms", ignore = true)
    PlageHoraireResponse toResponse(PlageHoraire plageHoraire);

    List<PlageHoraireResponse> toResponseList(List<PlageHoraire> plages);
}
