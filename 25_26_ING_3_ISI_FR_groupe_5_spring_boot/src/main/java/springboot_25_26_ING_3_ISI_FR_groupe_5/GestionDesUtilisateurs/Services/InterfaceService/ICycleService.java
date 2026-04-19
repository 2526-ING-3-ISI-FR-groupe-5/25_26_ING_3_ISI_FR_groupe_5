package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService;

import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Cycle;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;

import java.util.List;

public interface ICycleService {

    Cycle creer(Cycle cycle, Utilisateur acteur);

    Cycle modifier(Long id, Cycle data, Utilisateur acteur);

    Cycle findById(Long id);

    List<Cycle> getAll();

    List<Cycle> getByEcole(Long ecoleId);

    void supprimer(Long id, Utilisateur acteur);
}