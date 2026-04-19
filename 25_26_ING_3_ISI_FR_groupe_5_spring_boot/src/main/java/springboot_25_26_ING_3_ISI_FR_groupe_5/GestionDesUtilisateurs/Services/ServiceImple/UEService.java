package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Specialite;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.UE;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.TypeAction;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exceptions.DuplicateResourceException;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exceptions.ResourceNotFoundException;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.UERepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService.IUEService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService.IJournalActionService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UEService implements IUEService {

    private final UERepository ueRepo;
    private final SpecialiteService specialiteService;
    private final IJournalActionService journalService;

    @Override
    @Transactional
    public UE creer(UE ue, Long specialiteId, Utilisateur acteur) {
        try {
            Specialite specialite = specialiteService.findById(specialiteId);

            if (ueRepo.findByCode(ue.getCode()).isPresent()) {
                throw new DuplicateResourceException("UE", ue.getCode());
            }

            ue.setSpecialite(specialite);
            UE saved = ueRepo.save(ue);

            // ✅ Journalisation
            journalService.journaliserCreationUE(acteur, saved.getId(), saved.getNom());

            return saved;

        } catch (DuplicateResourceException e) {
            journalService.journaliserEchec(acteur, TypeAction.UE_CREEE,
                    "UE", null, e.getMessage());
            throw e;
        } catch (Exception e) {
            journalService.journaliserEchec(acteur, TypeAction.UE_CREEE,
                    "UE", null, e.getMessage());
            throw new RuntimeException("Erreur lors de la création de l'UE", e);
        }
    }

    @Override
    @Transactional
    public UE modifier(Long id, UE data, Utilisateur acteur) {
        try {
            UE ue = findById(id);
            ue.setNom(data.getNom());
            ue.setCode(data.getCode());
            ue.setLibelle(data.getLibelle());
            ue.setLibelleAnglais(data.getLibelleAnglais());

            UE saved = ueRepo.save(ue);

            // ✅ Journalisation
            journalService.journaliserModificationUE(acteur, saved.getId(), saved.getNom());

            return saved;

        } catch (Exception e) {
            journalService.journaliserEchec(acteur, TypeAction.UE_MODIFIEE,
                    "UE", id, e.getMessage());
            throw new RuntimeException("Erreur lors de la modification de l'UE", e);
        }
    }

    @Override
    public UE findById(Long id) {
        return ueRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UE", "id", id));
    }

    @Override
    public List<UE> getAll() {
        return ueRepo.findAllUes();
    }

    @Override
    public List<UE> getByAnnee(Long anneeId) {
        return ueRepo.findAllUes();
    }

    @Override
    public List<UE> getBySpecialite(Long specialiteId) {
        return ueRepo.findBySpecialiteId(specialiteId);
    }

    @Override
    public List<UE> rechercher(String nom) {
        return ueRepo.findByNomContainingIgnoreCase(nom);
    }

    @Override
    @Transactional
    public void supprimer(Long id, Utilisateur acteur) {
        try {
            UE ue = findById(id);
            if (!ue.getProgrammations().isEmpty()) {
                throw new RuntimeException("Impossible de supprimer : cette UE est programmée dans " +
                        ue.getProgrammations().size() + " programme(s)");
            }
            ueRepo.delete(ue);

            // ✅ Journalisation
            journalService.journaliserSuppressionUE(acteur, id, ue.getNom());

        } catch (RuntimeException e) {
            journalService.journaliserEchec(acteur, TypeAction.UE_SUPPRIMEE,
                    "UE", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            journalService.journaliserEchec(acteur, TypeAction.UE_SUPPRIMEE,
                    "UE", id, e.getMessage());
            throw new RuntimeException("Erreur lors de la suppression de l'UE", e);
        }
    }
}