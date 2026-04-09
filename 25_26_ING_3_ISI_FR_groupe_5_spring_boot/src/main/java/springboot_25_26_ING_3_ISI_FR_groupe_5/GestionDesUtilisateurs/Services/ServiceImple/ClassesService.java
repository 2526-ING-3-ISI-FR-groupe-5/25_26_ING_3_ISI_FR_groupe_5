package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Classe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Niveau;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.ClassesRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassesService {

    private final ClassesRepository classesRepo;
    private final NiveauService niveauService;
    private final AnneeAcademiqueService anneeService;

    @Transactional
    public Classe creer(String nom, Long niveauId) {
        Niveau niveau = niveauService.findById(niveauId);

        if (classesRepo.existsByNomAndNiveauId(nom, niveauId)) {
            throw new RuntimeException("Une classe avec ce nom existe déjà pour ce niveau");
        }

        Classe classe = Classe.builder()
                .nom(nom)
                .niveau(niveau)
                .build();

        return classesRepo.save(classe);
    }

    @Transactional
    public Classe modifier(Long id, String nom, Long niveauId) {
        Classe classe = findById(id);

        if (!classe.getNom().equals(nom) && classesRepo.existsByNomAndNiveauId(nom, niveauId)) {
            throw new RuntimeException("Une classe avec ce nom existe déjà pour ce niveau");
        }

        classe.setNom(nom);
        classe.setNiveau(niveauService.findById(niveauId));
        return classesRepo.save(classe);
    }

    public Classe findById(Long id) {
        return classesRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Classe introuvable"));
    }

    public List<Classe> getAll() {
        return classesRepo.findAll();
    }

    public List<Classe> getByNiveau(Long niveauId) {
        niveauService.findById(niveauId);
        return classesRepo.findByNiveauId(niveauId);
    }

    public List<Classe> getByFiliere(Long filiereId) {
        return classesRepo.findByNiveau_Filiere_Id(filiereId);
    }

    public Page<Classe> getByAnnee(Long anneeId, String nom, Pageable pageable) {
        if (anneeId == null) {
            if (nom != null && !nom.isEmpty()) {
                // ✅ Correction : convertir List en Page
                List<Classe> classes = classesRepo.findByNomContainingIgnoreCase(nom);
                return new PageImpl<>(classes, pageable, classes.size());
            }
            return classesRepo.findAll(pageable);
        }
        anneeService.findById(anneeId);
        return classesRepo.searchByAnnee(anneeId, nom, pageable);
    }

    public List<Classe> rechercher(String nom) {
        return classesRepo.findByNomContainingIgnoreCase(nom);
    }

    @Transactional
    public void supprimer(Long id) {
        Classe classe = findById(id);
        if (classe.getInscriptions() != null && !classe.getInscriptions().isEmpty()) {
            throw new RuntimeException("Impossible de supprimer : cette classe contient " +
                    classe.getInscriptions().size() + " inscription(s)");
        }
        if (classe.getProgrammations() != null && !classe.getProgrammations().isEmpty()) {
            throw new RuntimeException("Impossible de supprimer : cette classe contient " +
                    classe.getProgrammations().size() + " programmation(s)");
        }
        classesRepo.delete(classe);
    }
}