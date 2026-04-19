package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Institut;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.TypeAction;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exceptions.DuplicateResourceException;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exceptions.ResourceNotFoundException;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.InstitutRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService.IInstitutService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService.IJournalActionService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InstitutService implements IInstitutService {

    private final InstitutRepository institutRepository;
    private final IJournalActionService journalService;

    @Override
    @Transactional
    public Institut creer(Institut institut, Utilisateur acteur) {
        try {
            if (institutRepository.existsByNomIgnoreCase(institut.getNom())) {
                throw new DuplicateResourceException("Institut", institut.getNom());
            }

            Institut saved = institutRepository.save(institut);

            // ✅ Journalisation
            journalService.journaliserCreationInstitut(acteur, saved.getId(), saved.getNom());

            return saved;

        } catch (DuplicateResourceException e) {
            journalService.journaliserEchec(acteur, TypeAction.INSTITUT_CREE,
                    "Institut", null, e.getMessage());
            throw e;
        } catch (Exception e) {
            journalService.journaliserEchec(acteur, TypeAction.INSTITUT_CREE,
                    "Institut", null, e.getMessage());
            throw new RuntimeException("Erreur lors de la création de l'institut", e);
        }
    }

    @Override
    @Transactional
    public Institut modifier(Long id, Institut institutModifie, Utilisateur acteur) {
        try {
            Institut existant = findById(id);

            if (!existant.getNom().equalsIgnoreCase(institutModifie.getNom()) &&
                    institutRepository.existsByNomIgnoreCase(institutModifie.getNom())) {
                throw new DuplicateResourceException("Institut", institutModifie.getNom());
            }

            existant.setNom(institutModifie.getNom());
            existant.setVille(institutModifie.getVille());
            existant.setAdresse(institutModifie.getAdresse());
            existant.setEmail(institutModifie.getEmail());
            existant.setTelephone(institutModifie.getTelephone());
            existant.setLocalite(institutModifie.getLocalite());

            Institut saved = institutRepository.save(existant);

            // ✅ Journalisation
            journalService.journaliserModificationInstitut(acteur, saved.getId(), saved.getNom());

            return saved;

        } catch (DuplicateResourceException e) {
            journalService.journaliserEchec(acteur, TypeAction.INSTITUT_MODIFIE,
                    "Institut", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            journalService.journaliserEchec(acteur, TypeAction.INSTITUT_MODIFIE,
                    "Institut", id, e.getMessage());
            throw new RuntimeException("Erreur lors de la modification de l'institut", e);
        }
    }

    @Override
    public Institut findById(Long id) {
        return institutRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Institut", "id", id));
    }

    @Override
    public List<Institut> getAll() {
        return institutRepository.findAll(Sort.by("nom").ascending());
    }

    @Override
    public Page<Institut> getAllPaginated(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("nom").ascending());
        if (search != null && !search.isEmpty()) {
            return institutRepository.search(search, pageable);
        }
        return institutRepository.findAll(pageable);
    }

    @Override
    public List<Institut> getByVille(String ville) {
        return institutRepository.findByVilleContainingIgnoreCase(ville);
    }

    @Override
    @Transactional
    public void supprimer(Long id, Utilisateur acteur) {
        try {
            Institut institut = findById(id);

            if (institut.getEcoles() != null && !institut.getEcoles().isEmpty()) {
                throw new RuntimeException("Impossible de supprimer l'institut car il contient " +
                        institut.getEcoles().size() + " école(s). Supprimez d'abord les écoles.");
            }

            institutRepository.delete(institut);

            // ✅ Journalisation
            journalService.journaliserSuppressionInstitut(acteur, id, institut.getNom());

        } catch (RuntimeException e) {
            journalService.journaliserEchec(acteur, TypeAction.INSTITUT_SUPPRIME,
                    "Institut", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            journalService.journaliserEchec(acteur, TypeAction.INSTITUT_SUPPRIME,
                    "Institut", id, e.getMessage());
            throw new RuntimeException("Erreur lors de la suppression de l'institut", e);
        }
    }

    @Override
    public long count() {
        return institutRepository.count();
    }
}