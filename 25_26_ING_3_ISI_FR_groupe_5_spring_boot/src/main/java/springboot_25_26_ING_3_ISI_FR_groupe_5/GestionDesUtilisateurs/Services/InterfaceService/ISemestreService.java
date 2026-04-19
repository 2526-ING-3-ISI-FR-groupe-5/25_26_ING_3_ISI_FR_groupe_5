package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService;

import springboot_25_26_ING_3_ISI_FR_groupe_5.Enums.TypeSemestre;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.semestre.SemestreRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.semestre.SemestreResponse;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;

import java.util.List;

public interface ISemestreService {

    SemestreResponse creer(SemestreRequest request, Utilisateur auteur);
    SemestreResponse activerSemestre(Long id, Utilisateur auteur);
    SemestreResponse desactiverSemestre(Long id, Utilisateur auteur);
    SemestreResponse findById(Long id);
    List<SemestreResponse> getByAnnee(Long anneeId);
    SemestreResponse getSemestreActif(Long anneeId);
    SemestreResponse getByAnneeAndType(Long anneeId, TypeSemestre type);
    long countByAnnee(Long anneeId);
    boolean isMaxSemestresReached(Long anneeId);
    void supprimer(Long id, Utilisateur auteur);
}