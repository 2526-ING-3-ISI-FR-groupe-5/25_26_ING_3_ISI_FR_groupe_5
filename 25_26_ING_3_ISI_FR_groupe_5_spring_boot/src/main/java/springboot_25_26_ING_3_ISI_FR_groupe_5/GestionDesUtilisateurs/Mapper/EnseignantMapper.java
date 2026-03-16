package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mapper;

import org.mapstruct.Mapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Response.EnseignantResponseDTO;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Response.EnseignantResponseDetails;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;

import java.util.List;

@Mapper(componentModel = "spring",  uses = {RoleMapper.class})
public interface EnseignantMapper {
    EnseignantResponseDTO  toDTO(Enseignant enseignant);

    EnseignantResponseDetails toDTOdetails(Enseignant enseignant);

    default List<EnseignantResponseDTO> toDTOList(List<Enseignant> enseignants){
        return enseignants.stream().map(this::toDTO).toList() ;
    }
}
