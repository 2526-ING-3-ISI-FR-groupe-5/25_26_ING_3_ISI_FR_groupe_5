package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Semestre;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.semestre.SemestreRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.semestre.SemestreResponse;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SemestreMapper {

    @Mapping(target = "libelle", source = "typeSemestre.libelle")
    @Mapping(target = "numero", source = "typeSemestre.numero")
    @Mapping(target = "anneeAcademiqueId", source = "anneeAcademique.id")
    @Mapping(target = "anneeAcademiqueNom", source = "anneeAcademique.nom")
    SemestreResponse toResponse(Semestre semestre);

    List<SemestreResponse> toResponseList(List<Semestre> semestres);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "anneeAcademique", ignore = true)
    Semestre toEntity(SemestreRequest request);
}