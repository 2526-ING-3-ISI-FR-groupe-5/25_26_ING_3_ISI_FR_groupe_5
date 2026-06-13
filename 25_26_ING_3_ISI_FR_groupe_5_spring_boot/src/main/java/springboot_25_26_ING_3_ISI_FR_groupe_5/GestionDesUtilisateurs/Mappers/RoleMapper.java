package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers;

import org.mapstruct.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.role.ActiveRoleRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.role.RoleResponse;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Role;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {PermissionMapper.class},
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface RoleMapper {

    RoleResponse toDTO(Role role);

    ActiveRoleRequest toActiveRoleDTORequest(Role role);

    @Mapping(target = "active", ignore = true)
    @Mapping(target = "nom",        ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "createdAt",     ignore = true)
    @Mapping(target = "updateAt",    ignore = true)
    @Mapping(target = "permissions", ignore = true)
    void updateRoleFromDTO(ActiveRoleRequest activeRoleDTORequest, @MappingTarget Role role);

    // ✅ Liste
    default List<RoleResponse> toDTORole(List<Role> roles) {
        return roles.stream().map(this::toDTO).toList();
    }
}