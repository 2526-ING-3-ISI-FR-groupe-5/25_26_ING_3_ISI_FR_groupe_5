package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService;

import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Niveau;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;

import java.util.List;
import java.util.Optional;

public interface INiveauService {

    Niveau creer(Niveau niveau, Long filiereId, Long specialiteId, Utilisateur acteur);

    Niveau modifier(Long id, Niveau data, Long specialiteId, Utilisateur acteur);

    Niveau findById(Long id);

    List<Niveau> getAll();

    List<Niveau> getByFiliere(Long filiereId);

    List<Niveau> getBySpecialite(Long specialiteId);

    Optional<Niveau> getNiveauSuperieur(Niveau actuel);

    void supprimer(Long id, Utilisateur acteur);
}