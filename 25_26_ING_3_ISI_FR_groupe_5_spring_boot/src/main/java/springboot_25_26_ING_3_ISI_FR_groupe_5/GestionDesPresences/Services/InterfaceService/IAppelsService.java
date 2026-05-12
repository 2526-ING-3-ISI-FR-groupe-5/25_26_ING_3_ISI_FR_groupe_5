package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Services.InterfaceService;

import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.appel.AppelRetardRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.appel.AppelsCheckManuelRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.appel.AppelsRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.Appels;

import java.util.List;

public interface IAppelsService {

    // Recherche
    Appels findById(Long id);
    List<Appels> getByPlageHoraire(Long id);
    List<Appels> getByEtudiant(Long id);
    List<Appels> getBySession(Long id);
    List<Appels> getRetardsByPlage(Long id);

    @Transactional
    void ajusterHeures(Long appelId, int nbHeuresPresent);

    List<Appels> getRetardsByEtudiant(Long etudiantId);

    // Actions
    Appels creer(AppelsRequest req);
    List<Appels> enregistrerAppelManuel(AppelsCheckManuelRequest req);
    Appels marquerRetard(AppelRetardRequest req);
    Appels modifier(Long id, AppelsRequest req);
    void supprimer(Long id);

    // Validation par code
    Appels validerParCode(AppelsRequest req, Long etudiantId);
}
