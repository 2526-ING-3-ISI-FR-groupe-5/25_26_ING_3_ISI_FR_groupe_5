package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService;

import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.role.RoleResponse;

import java.util.List;

public interface RoleServiceInter {
    List<RoleResponse> findAll();
    RoleResponse findById(Long id);

}
