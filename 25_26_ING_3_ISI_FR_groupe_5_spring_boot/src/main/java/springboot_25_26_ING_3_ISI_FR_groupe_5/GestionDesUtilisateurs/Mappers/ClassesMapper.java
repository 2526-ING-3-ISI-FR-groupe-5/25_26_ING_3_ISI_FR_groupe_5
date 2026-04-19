package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Classe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.classe.ClassesRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.classe.ClassesResponse;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClassesMapper {

    @Mapping(target = "niveauId", source = "niveau.id")
    @Mapping(target = "niveauNom", source = "niveau.nom")
    @Mapping(target = "niveauOrdre", source = "niveau.ordre")
    // ✅ Spécialité via le niveau
    @Mapping(target = "specialiteId", source = "niveau.specialite.id")
    @Mapping(target = "specialiteNom", source = "niveau.specialite.nom")
    @Mapping(target = "specialiteCode", source = "niveau.specialite.code")
    ClassesResponse toResponse(Classe classes);

    List<ClassesResponse> toResponseList(List<Classe> classes);

    @Mapping(target = "id", ignore = true)

    @Mapping(target = "niveau", ignore = true)
    @Mapping(target = "plagesHoraires", ignore = true)
    @Mapping(target = "inscriptions", ignore = true)
    @Mapping(target = "programmations", ignore = true)
    Classe toEntity(ClassesRequest request);
}