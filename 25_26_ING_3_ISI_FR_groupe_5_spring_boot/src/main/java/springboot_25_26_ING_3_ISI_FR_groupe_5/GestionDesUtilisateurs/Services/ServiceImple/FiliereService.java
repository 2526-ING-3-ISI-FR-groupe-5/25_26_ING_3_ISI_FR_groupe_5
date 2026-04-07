package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Cycle;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Ecole;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Filiere;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.FiliereRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FiliereService {

    private final FiliereRepository filiereRepo;
    private final EcoleService ecoleService;
    private final CycleService  cycleService;

    @Transactional
    public Filiere creer(Filiere filiere, Long ecoleId, Long cycleId) {

        Ecole ecole = ecoleService.findById(ecoleId);
        Cycle cycle = cycleService.findById(cycleId);

        if (filiereRepo.existsByCodeAndEcoleId(filiere.getCode(), ecoleId)) {
            throw new RuntimeException(
                    "Une filière avec ce code existe déjà dans cette école"
            );
        }

        filiere.setEcole(ecole);  // ✅ ecole et non code
        filiere.setCycle(cycle);
        return filiereRepo.save(filiere);
    }
    // ✅ Ajouter cette méthode
    @Transactional
    public List<Filiere> getAll() {
        return filiereRepo.findAll();
    }
    @Transactional
    public Filiere modifier(Long id, Filiere data) {
        Filiere filiere = findById(id);
        filiere.setNom(data.getNom());
        filiere.setCode(data.getCode());
        filiere.setDescription(data.getDescription());
        return filiereRepo.save(filiere);
    }

    public Filiere findById(Long id) {
        return filiereRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Filière introuvable"));
    }

    public List<Filiere> getByEcole(Long ecoleId) {
        ecoleService.findById(ecoleId);
        return filiereRepo.findByEcoleId(ecoleId);
    }

    public List<Filiere> getByCycle(Long cycleId) {
        cycleService.findById(cycleId);
        return filiereRepo.findByCycleId(cycleId);
    }

    public List<Filiere> getByEcoleAndCycle(Long ecoleId, Long cycleId) {
        return filiereRepo.findByEcoleIdAndCycleId(ecoleId, cycleId);
    }

    public List<Filiere> rechercher(String nom) {
        return filiereRepo.findByNomContainingIgnoreCase(nom);
    }

    @Transactional
    public void supprimer(Long id) {
        Filiere filiere = findById(id);
        if (!filiere.getSpecialites().isEmpty()) {
            throw new RuntimeException(
                    "Impossible de supprimer : cette filière contient des spécialités"
            );
        }
        if (!filiere.getNiveaux().isEmpty()) {
            throw new RuntimeException(
                    "Impossible de supprimer : cette filière contient des niveaux"
            );
        }
        filiereRepo.delete(filiere);
    }
}
