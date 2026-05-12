package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Etudiant;

public interface IEtudiantService {

    // CRUD
    Etudiant creer(Etudiant etudiant, Long classeId);
    Etudiant modifier(Long id, Etudiant data);
    Etudiant toggleActif(Long id);

    // Recherche
    Etudiant findById(Long id);
    Page<Etudiant> rechercher(Long anneeId, String recherche, Pageable pageable);

    // Gestion mot de passe
    void reinitialiserMotDePasse(Long id);
}
