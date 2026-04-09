package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Classe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.assistant.AssistantRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.assistant.AssistantResponse;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.AssistantPedagogique;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface AssistantMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "roles", ignore = true)
    AssistantPedagogique toEntity(AssistantRequest request);

    @Mapping(target = "classesNoms", source = "classes", qualifiedByName = "mapClassesNoms")
    AssistantResponse toResponse(AssistantPedagogique assistant);

    List<AssistantResponse> toResponseList(List<AssistantPedagogique> assistants);

    @Named("mapClassesNoms")
    default List<String> mapClassesNoms(java.util.Collection<Classe> classes) {
        if (classes == null) return List.of();
        return classes.stream()
                .map(Classe::getNom)
                .collect(Collectors.toList());
    }
}