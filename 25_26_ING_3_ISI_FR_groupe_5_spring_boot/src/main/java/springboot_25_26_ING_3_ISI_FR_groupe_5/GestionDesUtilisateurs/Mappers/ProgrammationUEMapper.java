package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.programmation.ProgrammationRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.programmation.ProgrammationResponse;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.ProgrammationUE;

import java.util.List;

@Mapper(componentModel = "spring", uses = {EnseignantMapper.class})
public interface ProgrammationUEMapper {

    // ══════════════════════════════════════════
    // RESPONSE (pour l'affichage)
    // ══════════════════════════════════════════

    @Mapping(target = "ueId", source = "ue.id")
    @Mapping(target = "ueNom", source = "ue.nom")
    @Mapping(target = "ueCode", source = "ue.code")
    @Mapping(target = "semestreId", source = "semestre.id")
    @Mapping(target = "semestreNom", source = "semestre.typeSemestre.libelle")
    @Mapping(target = "classeId", source = "classe.id")
    @Mapping(target = "classeNom", source = "classe.nom")
    ProgrammationResponse toResponse(ProgrammationUE programmation);

    List<ProgrammationResponse> toResponseList(List<ProgrammationUE> programmations);

    // ══════════════════════════════════════════
    // REQUEST (pour la création/modification)
    // ══════════════════════════════════════════

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ue", ignore = true)
    @Mapping(target = "semestre", ignore = true)
    @Mapping(target = "classe", ignore = true)
    @Mapping(target = "enseignants", ignore = true)
    @Mapping(target = "plagesHoraires", ignore = true)
    @Mapping(target = "libelle", ignore = true)
    @Mapping(target = "libelleAnglais", ignore = true)
    ProgrammationUE toEntity(ProgrammationRequest request);
}