package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Cycle;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.TypeAction;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exceptions.DuplicateResourceException;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exceptions.ResourceNotFoundException;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exceptions.TypeServiceREQUIRE;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.CycleRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService.ICycleService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService.IJournalActionService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CycleService implements ICycleService {

    private final CycleRepository cycleRepo;
    private final IJournalActionService journalService;

    @Override
    @Transactional
    public Cycle creer(Cycle cycle, Utilisateur acteur) {
        try {
            if (cycle.getTypeCycle() == null) {
                throw new RuntimeException("Le type de cycle est obligatoire");
            }

            if (cycleRepo.existsByTypeCycle(cycle.getTypeCycle())) {
                throw new DuplicateResourceException("Cycle", cycle.getTypeCycle().getLibelle());
            }

            Cycle saved = cycleRepo.save(cycle);

            // ✅ Journalisation
            journalService.journaliserCreationCycle(acteur, saved.getId(), saved.getTypeCycle().getLibelle());

            return saved;

        } catch (DuplicateResourceException e) {
            journalService.journaliserEchec(acteur, TypeAction.CYCLE_CREE,
                    "Cycle", null, e.getMessage());
            throw e;
        } catch (Exception e) {
            journalService.journaliserEchec(acteur, TypeAction.CYCLE_CREE,
                    "Cycle", null, e.getMessage());
            throw new RuntimeException("Erreur lors de la création du cycle", e);
        }
    }

    @Override
    @Transactional
    public Cycle modifier(Long id, Cycle data, Utilisateur acteur) {
        try {
            Cycle cycle = findById(id);

            if (data.getTypeCycle() == null) {
                throw new TypeServiceREQUIRE("Le type de cycle est obligatoire");
            }

            cycle.setTypeCycle(data.getTypeCycle());
            Cycle saved = cycleRepo.save(cycle);

            // ✅ Journalisation
            journalService.journaliserModificationCycle(acteur, saved.getId(), saved.getTypeCycle().getLibelle());

            return saved;

        } catch (TypeServiceREQUIRE e) {
            journalService.journaliserEchec(acteur, TypeAction.CYCLE_MODIFIE,
                    "Cycle", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            journalService.journaliserEchec(acteur, TypeAction.CYCLE_MODIFIE,
                    "Cycle", id, e.getMessage());
            throw new RuntimeException("Erreur lors de la modification du cycle", e);
        }
    }

    @Override
    public Cycle findById(Long id) {
        return cycleRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cycle", "id", id));
    }

    @Override
    public List<Cycle> getAll() {
        return cycleRepo.findAll();
    }

    @Override
    public List<Cycle> getByEcole(Long ecoleId) {
        return cycleRepo.findByEcoleId(ecoleId);
    }

    @Override
    @Transactional
    public void supprimer(Long id, Utilisateur acteur) {
        try {
            Cycle cycle = findById(id);

            if (cycle.getFilieres() != null && !cycle.getFilieres().isEmpty()) {
                throw new RuntimeException("Impossible de supprimer le cycle car il contient " +
                        cycle.getFilieres().size() + " filière(s). Supprimez d'abord les filières.");
            }

            cycleRepo.delete(cycle);

            // ✅ Journalisation
            journalService.journaliserSuppressionCycle(acteur, id, cycle.getTypeCycle().getLibelle());

        } catch (RuntimeException e) {
            journalService.journaliserEchec(acteur, TypeAction.CYCLE_SUPPRIME,
                    "Cycle", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            journalService.journaliserEchec(acteur, TypeAction.CYCLE_SUPPRIME,
                    "Cycle", id, e.getMessage());
            throw new RuntimeException("Erreur lors de la suppression du cycle", e);
        }
    }
}