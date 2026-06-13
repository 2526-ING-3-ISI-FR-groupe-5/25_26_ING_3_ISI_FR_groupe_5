package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.permission.ActivePermissionRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.permission.PermissionResponse;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Permission;

import java.util.List;

@Mapper(componentModel = "spring" )
public interface PermissionMapper {

    @Mapping(target = "creatAt", ignore = true)
    PermissionResponse toDTO(Permission permission);

    //--------------------------
    ActivePermissionRequest toActivePermmission(Permission permission);


    //--

    @Mapping(target = "nom", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "id", ignore = true)
    void  updatePermission(ActivePermissionRequest activePermissionRequest, @MappingTarget Permission  permission );

    //--------------
    default List<PermissionResponse> toDTO(List<Permission> permissions) {
        return permissions.stream().map(this::toDTO).toList();
    }
}

