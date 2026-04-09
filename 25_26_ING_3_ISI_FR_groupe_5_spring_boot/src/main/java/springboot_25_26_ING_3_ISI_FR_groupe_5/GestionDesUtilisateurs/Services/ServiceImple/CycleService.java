package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Cycle;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exceptions.DuplicateResourceException;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exceptions.ResourceNotFoundException;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exceptions.TypeServiceREQUIRE;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.CycleRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CycleService {

    private final CycleRepository cycleRepo;

    @Transactional
    public Cycle creer(Cycle cycle) {
        // Vérifier que le type n'est pas null
        if (cycle.getTypeCycle() == null) {
            throw new RuntimeException("Le type de cycle est obligatoire");
        }

        // Vérifier si le cycle existe déjà
        if (cycleRepo.existsByTypeCycle(cycle.getTypeCycle())) {
            throw new DuplicateResourceException("Cycle", cycle.getTypeCycle().getLibelle());
        }

        return cycleRepo.save(cycle);
    }

    @Transactional
    public Cycle modifier(Long id, Cycle data) {
        Cycle cycle = findById(id);

        // Vérifier que le nouveau type n'est pas null
        if (data.getTypeCycle() == null) {
            throw new TypeServiceREQUIRE("Le type de cycle est obligatoire");
        }

        cycle.setTypeCycle(data.getTypeCycle());
        return cycleRepo.save(cycle);
    }

    public Cycle findById(Long id) {
        return cycleRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cycle", "id", id));
    }

    public List<Cycle> getAll() {
        return cycleRepo.findAll();
    }

    public List<Cycle> getByEcole(Long ecoleId) {
        return cycleRepo.findByEcoleId(ecoleId);
    }

    @Transactional
    public void supprimer(Long id) {
        Cycle cycle = findById(id);

        // Vérifier si le cycle a des filières
        if (cycle.getFilieres() != null && !cycle.getFilieres().isEmpty()) {
            throw new RuntimeException("Impossible de supprimer le cycle car il contient " +
                    cycle.getFilieres().size() + " filière(s). Supprimez d'abord les filières.");
        }

        cycleRepo.delete(cycle);
    }
}