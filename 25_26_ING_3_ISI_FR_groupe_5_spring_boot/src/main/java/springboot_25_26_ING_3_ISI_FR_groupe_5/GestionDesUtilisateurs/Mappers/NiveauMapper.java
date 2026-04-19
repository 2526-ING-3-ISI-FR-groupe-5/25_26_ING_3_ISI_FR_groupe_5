package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers;

import jakarta.validation.Valid;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Niveau;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.niveau.NiveauRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.niveau.NiveauResponse;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NiveauMapper {

    @Mapping(target = "specialiteNom", source = "specialite.nom")
    @Mapping(target = "specialiteId", source = "specialite.id")
    @Mapping(target = "filiereId", source = "filiere.id")
    @Mapping(target = "filiereNom", source = "filiere.nom")
    NiveauResponse toResponse(Niveau niveau);

    List<NiveauResponse> toResponseList(List<Niveau> niveaux);

    @Mapping(target = "specialite", ignore = true)
    @Mapping(target = "id", ignore = true)

    @Mapping(target = "filiere", ignore = true)
    @Mapping(target = "classes", ignore = true)
    Niveau toEntity(@Valid NiveauRequest request);
}