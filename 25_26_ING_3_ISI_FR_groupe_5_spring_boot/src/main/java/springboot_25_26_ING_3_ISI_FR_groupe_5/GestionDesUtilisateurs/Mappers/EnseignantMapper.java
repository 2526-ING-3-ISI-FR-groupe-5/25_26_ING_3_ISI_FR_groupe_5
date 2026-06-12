package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers;

import org.mapstruct.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.enseignant.EnseignantRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.enseignant.EnseignantResponse;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.enseignant.EnseignantResponseDetails;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EnseignantMapper {

    EnseignantResponse toResponse(Enseignant enseignant);

    List<EnseignantResponse> toResponseList(List<Enseignant> enseignants);

    @BeanMapping(builder = @Builder(disableBuilder = true))
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "firstLogin", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "programmations", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    Enseignant toEntity(EnseignantRequest request);

    // ✅ Ajouté
    EnseignantResponseDetails toDtoDetails(Enseignant enseignant);
}