package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesJustificatifs.Services.InterfaceService;

import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesJustificatifs.Entity.Justificatif;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesJustificatifs.DTO.justificatif.JustificatifRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;

import java.util.List;

public interface IJustificatifService {

    Justificatif findById(Long id);
    List<Justificatif> getByEtudiant(Long etudiantId);
    List<Justificatif> getEnAttenteByClasse(Long classeId);
    List<Justificatif> getEnAttenteByInstitut(Long institutId);
    Justificatif soumettre(JustificatifRequest req, Utilisateur auteur);
    Justificatif valider(Long id, Utilisateur validateur, String commentaire);
    Justificatif refuser(Long id, Utilisateur validateur, String commentaire);
    Justificatif lierAppels(Long justificatifId, List<Long> appelIds);
    void supprimer(Long id, Utilisateur auteur);
}