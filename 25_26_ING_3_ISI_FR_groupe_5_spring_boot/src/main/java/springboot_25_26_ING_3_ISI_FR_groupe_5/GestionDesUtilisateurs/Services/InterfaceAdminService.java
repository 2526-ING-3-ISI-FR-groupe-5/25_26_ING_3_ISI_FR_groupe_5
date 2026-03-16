package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services;

import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Response.AssistantResponseDetails;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Response.EnseignantResponseDetails;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Response.UtilisateurResponseDTO;

public interface InterfaceAdminService {
    @Transactional
    Page<UtilisateurResponseDTO> listeTous(String recherche, String type, int page, int size);

    @Transactional
    void deleteUtilisateur(Long id);

    @Transactional
    EnseignantResponseDetails getEnseignant(Long id);

    @Transactional
    EnseignantResponseDetails EnsDetails(Long id);

    @Transactional
    AssistantResponseDetails AssDetails(Long id);
}
