package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.programmation.ProgrammationResponse;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.ProgrammationUE;

import java.util.List;

@Mapper(componentModel = "spring", uses = {EnseignantMapper.class})
public interface ProgrammationUEMapper {

    @Mapping(target = "ueId", source = "ue.id")
    @Mapping(target = "ueNom", source = "ue.nom")
    @Mapping(target = "ueCode", source = "ue.code")
    @Mapping(target = "semestreId", source = "semestre.id")
    // ✅ CORRECTION : semestre.typeSemestre.libelle au lieu de semestre.typeSemestre
    @Mapping(target = "semestreNom", source = "semestre.typeSemestre.libelle")
    @Mapping(target = "classeId", source = "classe.id")
    @Mapping(target = "classeNom", source = "classe.nom")
    ProgrammationResponse toResponse(ProgrammationUE programmation);

    List<ProgrammationResponse> toResponseList(List<ProgrammationUE> programmations);
}