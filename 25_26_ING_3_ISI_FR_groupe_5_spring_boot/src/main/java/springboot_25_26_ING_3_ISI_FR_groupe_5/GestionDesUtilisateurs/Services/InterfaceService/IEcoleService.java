package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService;

import org.springframework.data.domain.Page;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Ecole;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.ecole.EcoleRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;

import java.util.List;

public interface IEcoleService {

    Ecole creer(EcoleRequest request, Utilisateur acteur);

    Ecole modifier(Long id, EcoleRequest request, Utilisateur acteur);

    Ecole findById(Long id);

    List<Ecole> getAll();

    Page<Ecole> getAllPaginated(int page, int size, String search);

    List<Ecole> getByInstitut(Long institutId);

    Page<Ecole> getByInstitutPaginated(Long institutId, int page, int size);

    void supprimer(Long id, Utilisateur acteur);

    long count();
}