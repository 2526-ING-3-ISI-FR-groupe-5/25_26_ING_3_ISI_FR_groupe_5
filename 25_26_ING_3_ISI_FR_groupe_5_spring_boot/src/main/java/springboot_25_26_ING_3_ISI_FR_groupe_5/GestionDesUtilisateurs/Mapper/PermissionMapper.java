package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Response.PermissionResponseDTO;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Resquest.PermissionRequestDTO;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Permission;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Role;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    PermissionResponseDTO toDTO(Permission permission);

    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateCreation", ignore = true)
    Permission toEntity(PermissionRequestDTO permissionRequestDTO);
    default List<String> mapRoles(Set<Role> roles) {
        if (roles == null) return null;
        return roles.stream()
                .map(Role::getNom) // On extrait uniquement le nom du rôle
                .toList();
    }
}
