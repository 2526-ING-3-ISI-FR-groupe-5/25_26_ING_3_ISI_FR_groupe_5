package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Cycle;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Ecole;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Filiere;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.filiere.FiliereRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.TypeAction;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exceptions.DuplicateResourceException;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exceptions.ResourceNotFoundException;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.CycleRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.EcoleRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.FiliereRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService.IFiliereService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService.IJournalActionService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FiliereService implements IFiliereService {

    private final FiliereRepository filiereRepository;
    private final EcoleRepository ecoleRepository;
    private final CycleRepository cycleRepository;
    private final IJournalActionService journalService;

    @Override
    @Transactional
    public Filiere creer(FiliereRequest request, Utilisateur acteur) {
        try {
            Ecole ecole = ecoleRepository.findById(request.getEcoleId())
                    .orElseThrow(() -> new ResourceNotFoundException("École", "id", request.getEcoleId()));

            Cycle cycle = cycleRepository.findById(request.getCycleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cycle", "id", request.getCycleId()));

            if (filiereRepository.existsByCodeAndEcoleId(request.getCode(), request.getEcoleId())) {
                throw new DuplicateResourceException("Filière", "code " + request.getCode() + " dans l'école " + ecole.getNom());
            }

            Filiere filiere = new Filiere();
            filiere.setNom(request.getNom());
            filiere.setCode(request.getCode());
            filiere.setDescription(request.getDescription());
            filiere.setEcole(ecole);
            filiere.setCycle(cycle);

            Filiere saved = filiereRepository.save(filiere);

            // ✅ Journalisation
            journalService.journaliserCreationFiliere(acteur, saved.getId(), saved.getNom());

            return saved;

        } catch (DuplicateResourceException e) {
            journalService.journaliserEchec(acteur, TypeAction.FILIERE_CREEE,
                    "Filiere", null, e.getMessage());
            throw e;
        } catch (Exception e) {
            journalService.journaliserEchec(acteur, TypeAction.FILIERE_CREEE,
                    "Filiere", null, e.getMessage());
            throw new RuntimeException("Erreur lors de la création de la filière", e);
        }
    }

    @Override
    @Transactional
    public Filiere modifier(Long id, FiliereRequest request, Utilisateur acteur) {
        try {
            Filiere existante = findById(id);
            Ecole ecole = ecoleRepository.findById(request.getEcoleId())
                    .orElseThrow(() -> new ResourceNotFoundException("École", "id", request.getEcoleId()));
            Cycle cycle = cycleRepository.findById(request.getCycleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cycle", "id", request.getCycleId()));

            if (!existante.getCode().equals(request.getCode()) &&
                    filiereRepository.existsByCodeAndEcoleId(request.getCode(), request.getEcoleId())) {
                throw new DuplicateResourceException("Filière", "code " + request.getCode() + " dans l'école " + ecole.getNom());
            }

            existante.setNom(request.getNom());
            existante.setCode(request.getCode());
            existante.setDescription(request.getDescription());
            existante.setEcole(ecole);
            existante.setCycle(cycle);

            Filiere saved = filiereRepository.save(existante);

            // ✅ Journalisation
            journalService.journaliserModificationFiliere(acteur, saved.getId(), saved.getNom());

            return saved;

        } catch (DuplicateResourceException e) {
            journalService.journaliserEchec(acteur, TypeAction.FILIERE_MODIFIEE,
                    "Filiere", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            journalService.journaliserEchec(acteur, TypeAction.FILIERE_MODIFIEE,
                    "Filiere", id, e.getMessage());
            throw new RuntimeException("Erreur lors de la modification de la filière", e);
        }
    }

    @Override
    public Filiere findById(Long id) {
        return filiereRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Filière", "id", id));
    }

    @Override
    public List<Filiere> getAll() {
        return filiereRepository.findAll();
    }

    @Override
    public List<Filiere> getByEcole(Long ecoleId) {
        return filiereRepository.findByEcoleId(ecoleId);
    }

    @Override
    public List<Filiere> getByCycle(Long cycleId) {
        return filiereRepository.findByCycleId(cycleId);
    }

    @Override
    public List<Filiere> getByEcoleAndCycle(Long ecoleId, Long cycleId) {
        return filiereRepository.findByEcoleIdAndCycleId(ecoleId, cycleId);
    }

    @Override
    public List<Filiere> searchByNom(String nom) {
        return filiereRepository.findByNomContainingIgnoreCase(nom);
    }

    @Override
    public boolean existsByCodeAndEcole(String code, Long ecoleId) {
        return filiereRepository.existsByCodeAndEcoleId(code, ecoleId);
    }

    @Override
    @Transactional
    public void supprimer(Long id, Utilisateur acteur) {
        try {
            Filiere filiere = findById(id);

            if (filiere.getSpecialites() != null && !filiere.getSpecialites().isEmpty()) {
                throw new RuntimeException("Impossible de supprimer la filière car elle contient " +
                        filiere.getSpecialites().size() + " spécialité(s). Supprimez d'abord les spécialités.");
            }

            if (filiere.getNiveaux() != null && !filiere.getNiveaux().isEmpty()) {
                throw new RuntimeException("Impossible de supprimer la filière car elle contient " +
                        filiere.getNiveaux().size() + " niveau(x). Supprimez d'abord les niveaux.");
            }

            filiereRepository.delete(filiere);

            // ✅ Journalisation
            journalService.journaliserSuppressionFiliere(acteur, id, filiere.getNom());

        } catch (RuntimeException e) {
            journalService.journaliserEchec(acteur, TypeAction.FILIERE_SUPPRIMEE,
                    "Filiere", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            journalService.journaliserEchec(acteur, TypeAction.FILIERE_SUPPRIMEE,
                    "Filiere", id, e.getMessage());
            throw new RuntimeException("Erreur lors de la suppression de la filière", e);
        }
    }

    @Override
    public long count() {
        return filiereRepository.count();
    }
}