package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers;

import org.mapstruct.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.programmation.ProgrammationRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.programmation.ProgrammationResponse;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.ProgrammationUE;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProgrammationUEMapper {

    @Mapping(target = "ueId", source = "ue.id")
    @Mapping(target = "ueNom", source = "ue.nom")
    @Mapping(target = "ueCode", source = "ue.code")
    @Mapping(target = "ueLibelle", source = "ue.libelle")

    @Mapping(target = "semestreId", source = "semestre.id")
    @Mapping(target = "semestreNom", source = "semestre.typeSemestre")

    @Mapping(target = "classeId", source = "classe.id")
    @Mapping(target = "classeNom", source = "classe.nom")

    @Mapping(target = "institutId", expression = "java(programmation.getClasse() != null ? programmation.getClasse().getInstitutId() : null)")
    @Mapping(target = "institutNom", expression = "java(programmation.getClasse() != null ? programmation.getClasse().getInstitutNom() : null)")

    @Mapping(target = "enseignantsNoms", expression = "java(programmation.getEnseignants() != null ? programmation.getEnseignants().stream().map(e -> e.getPrenom() + \" \" + e.getNom()).toList() : java.util.List.of())")
    @Mapping(target = "nombreEnseignants", expression = "java(programmation.getEnseignants() != null ? programmation.getEnseignants().size() : 0)")

    @Mapping(target = "nombrePlagesHoraires", expression = "java(programmation.getPlagesHoraires() != null ? programmation.getPlagesHoraires().size() : 0)")

    ProgrammationResponse toResponse(ProgrammationUE programmation);

    List<ProgrammationResponse> toResponseList(List<ProgrammationUE> programmations);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ue", ignore = true)
    @Mapping(target = "semestre", ignore = true)
    @Mapping(target = "classe", ignore = true)
    @Mapping(target = "enseignants", ignore = true)
    @Mapping(target = "plagesHoraires", ignore = true)
    ProgrammationUE toEntity(ProgrammationRequest request);
}