package springboot_25_26_ING_3_ISI_FR_groupe_5.Mapper;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import springboot_25_26_ING_3_ISI_FR_groupe_5.DTO.PermissionSimpleDTO;
import springboot_25_26_ING_3_ISI_FR_groupe_5.DTO.Response.PermissionResponseDTO;
import springboot_25_26_ING_3_ISI_FR_groupe_5.DTO.Resquest.PermissionRequestDTO;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entites.Permission;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entites.Role;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Enums.TypeRole;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PermissionMapper {

    /**
     * Convertir RequestDTO → Entity
     */
    public Permission toEntity(PermissionRequestDTO requestDTO) {
        if (requestDTO == null) return null;

        Permission permission = new Permission();
        BeanUtils.copyProperties(requestDTO, permission, "roleIds");

        // Valeur par défaut
        if (permission.getActive() == null) {
            permission.setActive(true);
        }

        return permission;
    }

    /**
     * Convertir Entity → ResponseDTO (avec rôles)
     */
    public PermissionResponseDTO toResponseDTO(Permission permission) {
        if (permission == null) return null;

        PermissionResponseDTO responseDTO = new PermissionResponseDTO();
        BeanUtils.copyProperties(permission, responseDTO);

        // ✅ CORRECTION : Convertir TypeRole enum en String
        if (permission.getRoles() != null && !permission.getRoles().isEmpty()) {
            List<String> roleNames = permission.getRoles().stream()
                    .map(role -> {
                        // Récupérer le nom de l'enum et le convertir en String
                        TypeRole typeRole = role.getNom();  // getNom() retourne TypeRole
                        return typeRole.name();  // Convertit l'enum en String (ex: "ADMIN", "USER")
                    })
                    .collect(Collectors.toList());

            responseDTO.setRoles(roleNames);
        }

        return responseDTO;
    }

    /**
     * Convertir Entity → SimpleDTO (sans rôles)
     */
    public PermissionSimpleDTO toSimpleDTO(Permission permission) {
        if (permission == null) return null;

        PermissionSimpleDTO simpleDTO = new PermissionSimpleDTO();
        BeanUtils.copyProperties(permission, simpleDTO);
        return simpleDTO;
    }

    /**
     * Mettre à jour une entité existante
     */
    public void updateEntityFromDTO(PermissionRequestDTO requestDTO, Permission permission) {
        if (requestDTO == null || permission == null) return;

        BeanUtils.copyProperties(requestDTO, permission,
                "id", "roleIds", "dateCreation");
    }
}