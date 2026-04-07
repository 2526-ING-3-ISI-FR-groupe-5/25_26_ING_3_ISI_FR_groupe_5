package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers;

import org.mapstruct.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.ActiveRoleDTORequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Response.RoleResponseDTO;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Role;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {PermissionMapper.class},
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface RoleMapper {

    RoleResponseDTO toDTO(Role role);

    ActiveRoleDTORequest toActiveRoleDTORequest(Role role);

    @Mapping(target = "active", ignore = true)
    @Mapping(target = "nom",        ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "creatAt",     ignore = true)
    @Mapping(target = "updateAt",    ignore = true)
    @Mapping(target = "permissions", ignore = true)
    void updateRoleFromDTO(ActiveRoleDTORequest activeRoleDTORequest, @MappingTarget Role role);

    // ✅ Liste
    default List<RoleResponseDTO> toDTORole(List<Role> roles) {
        return roles.stream().map(this::toDTO).toList();
    }
}