package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService;

import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.ActivePermissionRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.ActiveRoleDTORequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Response.AssistantResponseDetails;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Response.EnseignantResponseDetails;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.assistant.AssistantRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.enseignant.EnseignantRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.utilisateur.SurveillantResponseDetails;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.utilisateur.UtilisateurRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.utilisateur.UtilisateurResponse;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.AssistantPedagogique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;

public interface InterfaceServiceAdmin {

  void activerDesactiverUtilisateur(Long id, boolean activer);

  Page<UtilisateurResponse> listeTous(String recherche, String type, int page, int size);

  UtilisateurResponse findById(Long id);

  void deleteUtilisateur(Long id);

  Enseignant getById(Long id);

  Enseignant save(EnseignantRequest enseignantRequestDTO);

  AssistantPedagogique saveAssistant(AssistantRequest assistantRequestDTO);

  EnseignantResponseDetails EnsDetails(Long id);

  @Transactional
  AssistantResponseDetails AssDetails(Long id);

  @Transactional
  SurveillantResponseDetails SurDetails(Long id); // ✅ Nouveau

  @Transactional
  ActiveRoleDTORequest activeRole(Long id, ActiveRoleDTORequest activeRoleDTORequest);

  @Transactional
  ActivePermissionRequest activePermissionRequest(Long id, ActivePermissionRequest activePermissionRequest);

  @Transactional
  void creerUtilisateur(UtilisateurRequest request);
}