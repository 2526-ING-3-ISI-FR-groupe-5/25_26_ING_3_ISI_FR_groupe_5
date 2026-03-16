package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Response.RoleResponseDTO;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Resquest.RoleUpdateDescriptionRequestDTO;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Role;

import java.util.List;

@Mapper(componentModel = "spring", uses = {PermissionMapper.class},
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleMapper {
    RoleResponseDTO toDTO(Role role);

    @Mapping(target = "nom", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateCreation", ignore = true)
    @Mapping(target = "active", ignore = true)
    Role updateDescritiontoEntity(RoleUpdateDescriptionRequestDTO roleUpdateDescriptionRequestDTO,
                                  @MappingTarget Role role);

    default List<RoleResponseDTO> toDTORole(List<Role> roles) {
        return roles.stream().map(this::toDTO).toList();
    }
}
