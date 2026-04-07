package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.assistant.AssistantRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.assistant.AssistantResponse;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.AssistantPedagogique;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AssistantMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "firstLogin", ignore = true)
    @Mapping(target = "locked", ignore = true)
    @Mapping(target = "expired", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "emailVerified", ignore = true)
    @Mapping(target = "phoneVerified", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "classes", ignore = true)
    AssistantPedagogique toEntity(AssistantRequest request);

    @Mapping(target = "classesNoms", ignore = true)
    AssistantResponse toResponse(AssistantPedagogique assistant);

    List<AssistantResponse> toResponseList(List<AssistantPedagogique> assistants);
}
