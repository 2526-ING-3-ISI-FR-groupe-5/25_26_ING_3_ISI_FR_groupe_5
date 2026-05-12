package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.InterfaceService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Classe;

import java.util.List;

public interface IClassesService {

    // Création et modification
    Classe creer(String nom, Long niveauId);
    Classe modifier(Long id, String nom, Long niveauId);
    void supprimer(Long id);

    // Recherche
    Classe findById(Long id);
    List<Classe> getAll();
    List<Classe> getByNiveau(Long niveauId);
    List<Classe> getByFiliere(Long filiereId);
    List<Classe> rechercher(String nom);
    Page<Classe> getByAnnee(Long anneeId, String nom, Pageable pageable);

    // Utilitaires
    List<Classe> getByInstitut(Long institutId);
    Long getAnneeAcademiqueActive();
}
