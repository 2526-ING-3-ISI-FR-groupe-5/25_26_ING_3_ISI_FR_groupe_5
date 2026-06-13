package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.permission.PermissionResponse;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.role.ActiveRoleRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.role.RoleResponse;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Permission;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Role;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exception.PermissionNotExistException;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exception.RoleIsNotExisteException;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers.PermissionMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers.RoleMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.PermissionRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.RoleRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService.IRoleService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService implements IRoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;

    // ══════════════════════════════════════════
    // RÔLES
    // ══════════════════════════════════════════

    @Transactional(readOnly = true)
    @Override
    public List<RoleResponse> listeRoles() {
        return roleMapper.toDTORole(roleRepository.findAllWithPermissions());
    }

    @Transactional(readOnly = true)
    @Override
    public List<PermissionResponse> listePermissions() {
        return permissionMapper.toDTO(permissionRepository.findAll());
    }

    @Transactional(readOnly = true)
    @Override
    public Role findById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new RoleIsNotExisteException("Role introuvable : " + id));
    }

    @Transactional
    @Override
    public Role activerRole(Long id, boolean active) {
        Role role = findById(id);
        role.setActive(active);
        if (!active) {
            role.getPermissions().forEach(p -> p.setActive(false));
            permissionRepository.saveAll(role.getPermissions());
        }
        Role saved = roleRepository.save(role);
        log.info("Role {} {}", saved.getNom(), active ? "active" : "desactive");
        return saved;
    }

    @Transactional
    @Override
    public RoleResponse updateRole(Long id, ActiveRoleRequest request) {
        Role role = findById(id);
        roleMapper.updateRoleFromDTO(request, role);
        Role saved = roleRepository.save(role);
        return roleMapper.toDTO(saved);
    }

    // ══════════════════════════════════════════
    // PERMISSIONS
    // ══════════════════════════════════════════

    @Transactional(readOnly = true)
    @Override
    public Permission findPermissionById(Long id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new PermissionNotExistException("Permission introuvable : " + id));
    }

    @Transactional
    @Override
    public Permission activerPermission(Long id, boolean active) {
        Permission permission = findPermissionById(id);
        permission.setActive(active);
        Permission saved = permissionRepository.save(permission);
        log.info("Permission {} {}", saved.getNom(), active ? "activee" : "desactivee");
        return saved;
    }

    @Transactional
    @Override
    public RoleResponse ajouterPermission(Long roleId, Long permissionId) {
        Role role = findById(roleId);
        Permission permission = findPermissionById(permissionId);
        role.addPermission(permission);
        Role saved = roleRepository.save(role);
        log.info("Permission {} ajoutee au role {}", permission.getNom(), saved.getNom());
        return roleMapper.toDTO(saved);
    }

    @Transactional
    @Override
    public RoleResponse retirerPermission(Long roleId, Long permissionId) {
        Role role = findById(roleId);
        Permission permission = findPermissionById(permissionId);
        role.removePermission(permission);
        Role saved = roleRepository.save(role);
        log.info("Permission {} retiree du role {}", permission.getNom(), saved.getNom());
        return roleMapper.toDTO(saved);
    }
}