package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService;

import org.springframework.data.domain.Page;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Institut;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;

import java.util.List;

public interface IInstitutService {

    Institut creer(Institut institut, Utilisateur acteur);

    Institut modifier(Long id, Institut institutModifie, Utilisateur acteur);

    Institut findById(Long id);

    List<Institut> getAll();

    Page<Institut> getAllPaginated(int page, int size, String search);

    List<Institut> getByVille(String ville);

    void supprimer(Long id, Utilisateur acteur);

    long count();
}