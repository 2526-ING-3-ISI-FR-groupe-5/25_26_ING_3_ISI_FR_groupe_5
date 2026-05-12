package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Surveillant;

public interface ISurveillantService {

    // CRUD
    Surveillant creer(Surveillant surveillant);
    Surveillant modifier(Long id, Surveillant data);
    Surveillant toggleActif(Long id);
    void supprimer(Long id);

    // Recherche
    Surveillant findById(Long id);
    Page<Surveillant> getAll(Pageable pageable);

    // Gestion mot de passe
    void reinitialiserMotDePasse(Long id);
}
