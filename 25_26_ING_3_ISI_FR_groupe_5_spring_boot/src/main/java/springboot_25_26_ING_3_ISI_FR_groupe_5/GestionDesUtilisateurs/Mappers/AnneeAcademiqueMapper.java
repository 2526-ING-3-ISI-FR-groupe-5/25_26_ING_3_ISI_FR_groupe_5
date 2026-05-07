package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Annee_academique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.annee.AnneeRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.annee.AnneeResponse;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AnneeAcademiqueMapper {

    @Mapping(target = "institutId", source = "institut.id")
    @Mapping(target = "institutNom", source = "institut.nom")
    @Mapping(target = "institutVille", source = "institut.ville")
    AnneeResponse toResponse(Annee_academique annee);

    List<AnneeResponse> toResponseList(List<Annee_academique> annees);

    @Mapping(target = "id", ignore = true)

    @Mapping(target = "semestres", ignore = true)
    @Mapping(target = "institut", ignore = true) // Sera défini dans le service
    Annee_academique toEntity(AnneeRequest request);
}