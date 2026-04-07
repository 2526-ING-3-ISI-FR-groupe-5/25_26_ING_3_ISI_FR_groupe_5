package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Etudiant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.etudiant.EtudiantRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.etudiant.EtudiantResponse;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EtudiantMapper {

    EtudiantResponse toResponse(Etudiant etudiant);

    List<EtudiantResponse> toResponseList(List<Etudiant> etudiants);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "inscriptions", ignore = true)
    @Mapping(target = "password", ignore = true)  // À encoder séparément
    Etudiant toEntity(EtudiantRequest request);
}