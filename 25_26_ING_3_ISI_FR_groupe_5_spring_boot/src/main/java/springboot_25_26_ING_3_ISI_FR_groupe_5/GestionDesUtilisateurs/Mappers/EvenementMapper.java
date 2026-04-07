package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Evenement;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.evenement.EvenementRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.evenement.EvenementResponse;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EvenementMapper {

    @Mapping(target = "anneeAcademiqueId", source = "anneeAcademique.id")
    @Mapping(target = "anneeAcademiqueNom", source = "anneeAcademique.nom")
    EvenementResponse toResponse(Evenement evenement);

    List<EvenementResponse> toResponseList(List<Evenement> evenements);

    @Mapping(target = "anneeAcademique", ignore = true) // rempli dans le service
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Evenement toEntity(EvenementRequest request);
}