package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Specialite;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.specialite.SpecialiteRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.specialite.SpecialiteResponse;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface SpecialiteMapper {

    @Mapping(target = "filiereId", source = "filiere.id")
    @Mapping(target = "filiereNom", source = "filiere.nom")
    @Mapping(target = "nombreNiveaux", source = "niveaux", qualifiedByName = "countNiveaux")
    SpecialiteResponse toResponse(Specialite specialite);

    List<SpecialiteResponse> toResponseList(List<Specialite> specialites);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "filiere", ignore = true)
    @Mapping(target = "niveaux", ignore = true)
    Specialite toEntity(SpecialiteRequest request);

    @Named("countNiveaux")
    default int countNiveaux(Collection<?> niveaux) {
        return niveaux != null ? niveaux.size() : 0;
    }
}