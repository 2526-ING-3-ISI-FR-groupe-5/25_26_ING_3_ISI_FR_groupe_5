package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService;

import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Response.RoleResponseDTO;

import java.util.List;

public interface RoleServiceInter {
    List<RoleResponseDTO> findAll();
    RoleResponseDTO findById(Long id);

}
