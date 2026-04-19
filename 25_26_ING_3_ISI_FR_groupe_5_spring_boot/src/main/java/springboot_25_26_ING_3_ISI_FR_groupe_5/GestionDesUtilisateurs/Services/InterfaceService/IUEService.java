package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService;

import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.UE;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;

import java.util.List;

public interface IUEService {

    UE creer(UE ue, Long specialiteId, Utilisateur acteur);

    UE modifier(Long id, UE data, Utilisateur acteur);

    UE findById(Long id);

    List<UE> getAll();

    List<UE> getByAnnee(Long anneeId);

    List<UE> getBySpecialite(Long specialiteId);

    List<UE> rechercher(String nom);

    void supprimer(Long id, Utilisateur acteur);
}