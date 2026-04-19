package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Specialite;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.TypeAction;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exceptions.DuplicateResourceException;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exceptions.ResourceNotFoundException;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.SpecialiteRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService.ISpecialiteService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService.IJournalActionService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpecialiteService implements ISpecialiteService {

    private final SpecialiteRepository specialiteRepository;
    private final IJournalActionService journalService;

    @Override
    @Transactional
    public Specialite creer(Specialite specialite, Utilisateur acteur) {
        try {
            if (specialite.getCode() == null || specialiteRepository.existsByCode(specialite.getCode())) {
                throw new DuplicateResourceException("Spécialité", specialite.getCode());
            }

            Specialite saved = specialiteRepository.save(specialite);

            // ✅ Journalisation
            journalService.journaliserCreationSpecialite(acteur, saved.getId(), saved.getNom());

            return saved;

        } catch (DuplicateResourceException e) {
            journalService.journaliserEchec(acteur, TypeAction.SPECIALITE_CREEE,
                    "Specialite", null, e.getMessage());
            throw e;
        } catch (Exception e) {
            journalService.journaliserEchec(acteur, TypeAction.SPECIALITE_CREEE,
                    "Specialite", null, e.getMessage());
            throw new RuntimeException("Erreur lors de la création de la spécialité", e);
        }
    }

    @Override
    @Transactional
    public Specialite modifier(Long id, Specialite data, Utilisateur acteur) {
        try {
            Specialite specialite = findById(id);

            if (!specialite.getCode().equals(data.getCode()) &&
                    specialiteRepository.existsByCode(data.getCode())) {
                throw new DuplicateResourceException("Spécialité", data.getCode());
            }

            specialite.setNom(data.getNom());
            specialite.setCode(data.getCode());
            specialite.setDescription(data.getDescription());

            Specialite saved = specialiteRepository.save(specialite);

            // ✅ Journalisation
            journalService.journaliserModificationSpecialite(acteur, saved.getId(), saved.getNom());

            return saved;

        } catch (DuplicateResourceException e) {
            journalService.journaliserEchec(acteur, TypeAction.SPECIALITE_MODIFIEE,
                    "Specialite", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            journalService.journaliserEchec(acteur, TypeAction.SPECIALITE_MODIFIEE,
                    "Specialite", id, e.getMessage());
            throw new RuntimeException("Erreur lors de la modification de la spécialité", e);
        }
    }

    @Override
    public Specialite findById(Long id) {
        return specialiteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Spécialité", "id", id));
    }

    @Override
    public List<Specialite> getAll() {
        return specialiteRepository.findAll();
    }

    @Override
    public List<Specialite> getByFiliere(Long filiereId) {
        return specialiteRepository.findByFiliereId(filiereId);
    }

    @Override
    @Transactional
    public void supprimer(Long id, Utilisateur acteur) {
        try {
            Specialite specialite = findById(id);

            if (specialite.getNiveaux() != null && !specialite.getNiveaux().isEmpty()) {
                throw new RuntimeException("Impossible de supprimer la spécialité car elle contient " +
                        specialite.getNiveaux().size() + " niveau(x). Supprimez d'abord les niveaux.");
            }

            specialiteRepository.delete(specialite);

            // ✅ Journalisation
            journalService.journaliserSuppressionSpecialite(acteur, id, specialite.getNom());

        } catch (RuntimeException e) {
            journalService.journaliserEchec(acteur, TypeAction.SPECIALITE_SUPPRIMEE,
                    "Specialite", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            journalService.journaliserEchec(acteur, TypeAction.SPECIALITE_SUPPRIMEE,
                    "Specialite", id, e.getMessage());
            throw new RuntimeException("Erreur lors de la suppression de la spécialité", e);
        }
    }
}