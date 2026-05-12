package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.AssistantPedagogique;

import java.util.List;

public interface IAssistantService {

    // CRUD
    AssistantPedagogique creer(AssistantPedagogique assistant, List<Long> classeIds);
    AssistantPedagogique modifier(Long id, AssistantPedagogique data);
    AssistantPedagogique toggleActif(Long id);
    void supprimer(Long id);

    // Recherche
    AssistantPedagogique findById(Long id);
    Page<AssistantPedagogique> rechercher(String recherche, Pageable pageable);

    // Gestion des classes
    AssistantPedagogique affecterClasses(Long id, List<Long> classeIds);

    // Gestion mot de passe
    void reinitialiserMotDePasse(Long id);
}
