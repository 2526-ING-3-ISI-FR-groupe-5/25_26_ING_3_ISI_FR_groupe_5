package springboot_25_26_ING_3_ISI_FR_groupe_5.Mapper;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import springboot_25_26_ING_3_ISI_FR_groupe_5.DTO.Resquest.RoleRequestDTO;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entites.Permission;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entites.Role;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entites.Utilisateur;

import java.util.stream.Collectors;

@Component
public class RoleMapper {

    /**
     * Convertit une entité Role en RoleResponseDTO
     * @param role l'entité à convertir
     * @return le DTO correspondant
     */
    public RoleRequestDTO toDTO(Role role) {
        if (role == null) {
            return null;
        }

        RoleRequestDTO dto = new RoleRequestDTO();

        // Copie des propriétés simples
        BeanUtils.copyProperties(role, dto, "permissions", "permission", "utilisateur");

        // Mapping manuel des relations (éviter la récursivité)
        // Note: Votre entité Role a deux collections de permissions (permissions et permission)
        // Je suppose que vous voulez utiliser "permission" (le Set)
        if (role.getPermission() != null) {
            dto.setPermissionIds(
                    role.getPermission().stream()
                            .map(Permission::getId)
                            .collect(Collectors.toList())
            );
        }

        if (role.getUtilisateur() != null) {
            dto.setUtilisateurIds(
                    role.getUtilisateur().stream()
                            .map(Utilisateur::getId)
                            .collect(Collectors.toList())
            );
        }

        return dto;
    }

    /**
     * Convertit un RoleResponseDTO en entité Role
     * @param dto le DTO à convertir
     * @return l'entité correspondante
     */
    public Role toEntity(RoleRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Role role = new Role();

        // Copie des propriétés simples
        BeanUtils.copyProperties(dto, role, "permissionIds", "utilisateurIds");

        // Note: Les relations (permissions, utilisateurs) doivent être gérées par le service
        // pour éviter les problèmes de cascade et de transaction

        return role;
    }

    /**
     * Met à jour une entité Role existante avec les données d'un DTO
     * @param dto le DTO source
     * @param role l'entité à mettre à jour
     */
    public void updateEntityFromDTO(RoleRequestDTO dto, Role role) {
        if (dto == null || role == null) {
            return;
        }

        // Copie des propriétés simples (ignore l'ID pour ne pas l'écraser)
        BeanUtils.copyProperties(dto, role, "id", "permissionIds", "utilisateurIds",
                "permissions", "permission", "utilisateur");

        // Les relations doivent être gérées par le service
    }
}