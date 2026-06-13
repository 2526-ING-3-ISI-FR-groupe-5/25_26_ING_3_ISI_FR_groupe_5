package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService;

import org.springframework.data.domain.Page;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.permission.ActivePermissionRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.role.ActiveRoleRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.assistant.AssistantResponseDetails;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.enseignant.EnseignantResponseDetails;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.assistant.AssistantRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.enseignant.EnseignantRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.utilisateur.SurveillantResponseDetails;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.utilisateur.UtilisateurRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.utilisateur.UtilisateurResponse;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.AssistantPedagogique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;

public interface InterfaceServiceAdmin {

  // LISTER
  Page<UtilisateurResponse> listeTous(String recherche, String type, Long anneeId, int page, int size);

  // TROUVER PAR ID
  UtilisateurResponse findById(Long id);

  // SUPPRIMER
  void deleteUtilisateur(Long id);

  // ACTIVER / DÉSACTIVER
  void activerDesactiverUtilisateur(Long id, boolean activer);

  // ENSEIGNANT
  Enseignant getById(Long id);
  Enseignant save(EnseignantRequest request);
  EnseignantResponseDetails EnsDetails(Long id);

  // ASSISTANT
  AssistantPedagogique saveAssistant(AssistantRequest request);
  AssistantResponseDetails AssDetails(Long id);

  // SURVEILLANT
  SurveillantResponseDetails SurDetails(Long id);

  // RÔLES
  ActiveRoleRequest activeRole(Long id, ActiveRoleRequest request);

  // PERMISSIONS
  ActivePermissionRequest activePermissionRequest(Long id, ActivePermissionRequest request);

  // CRÉER UTILISATEUR
  void creerUtilisateur(UtilisateurRequest request);
}