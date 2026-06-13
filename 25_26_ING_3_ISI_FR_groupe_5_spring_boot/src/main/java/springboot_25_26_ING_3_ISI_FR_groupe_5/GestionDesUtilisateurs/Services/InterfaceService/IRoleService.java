package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService;

import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.permission.PermissionResponse;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.role.ActiveRoleRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.role.RoleResponse;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Permission;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Role;

import java.util.List;

public interface IRoleService {

    @Transactional(readOnly = true)
    List<RoleResponse> listeRoles();

    @Transactional(readOnly = true)
    List<PermissionResponse> listePermissions();

    @Transactional(readOnly = true)
    Role findById(Long id);

    @Transactional
    Role activerRole(Long id, boolean active);

    @Transactional
    RoleResponse updateRole(Long id, ActiveRoleRequest request);

    @Transactional(readOnly = true)
    Permission findPermissionById(Long id);

    @Transactional
    Permission activerPermission(Long id, boolean active);

    @Transactional
    RoleResponse ajouterPermission(Long roleId, Long permissionId);

    @Transactional
    RoleResponse retirerPermission(Long roleId, Long permissionId);
}
