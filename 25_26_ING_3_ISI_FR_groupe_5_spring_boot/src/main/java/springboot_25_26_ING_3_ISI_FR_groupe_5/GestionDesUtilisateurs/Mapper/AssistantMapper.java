package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mapper;

import org.mapstruct.Mapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Response.AssistantResponseDetails;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.AssistantPedagogique;

import java.util.List;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface AssistantMapper {
    AssistantPedagogique toDTO(AssistantPedagogique assistant);

    AssistantResponseDetails toDTOdetails(AssistantPedagogique assistant);

    default List<AssistantPedagogique>toDTOList(List<AssistantPedagogique> assistants) {
        return assistants.stream().map(this::toDTO).toList();
    }
}
