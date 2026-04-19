package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Annee_academique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.TypeAction;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exceptions.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.AnneeAcademiqueRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.SemestreRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService.IAnneeAcademiqueService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService.IJournalActionService;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnneeAcademiqueService implements IAnneeAcademiqueService {

    private final AnneeAcademiqueRepository anneeRepo;
    private final SemestreRepository semestreRepo;
    private final IJournalActionService journalService;

    @Override
    @Transactional
    public Annee_academique creer(String nom, LocalDate dateDebut, LocalDate dateFin, boolean active, Utilisateur acteur) {

        try {
            if (anneeRepo.existsByNom(nom)) {
                throw new ANNEEACDEMIQUEEXISTEXCEPTION("L'année académique " + nom + " existe déjà");
            }

            if (dateDebut.isAfter(dateFin)) {
                throw new ANNEEACDEMIQUEEXISTEXCEPTION("La date de début doit être antérieure à la date de fin");
            }

            boolean overlap = anneeRepo.findAll().stream().anyMatch(annee ->
                    (dateDebut.isBefore(annee.getDateFin()) && dateFin.isAfter(annee.getDateDebut()))
            );

            if (overlap) {
                throw new ANNEEACDEMIQUEEXISTEXCEPTION("Les dates chevauchent une année académique existante");
            }

            Annee_academique annee = new Annee_academique();
            annee.setNom(nom);
            annee.setDateDebut(dateDebut);
            annee.setDateFin(dateFin);
            annee.setActive(active);

            Annee_academique saved = anneeRepo.save(annee);

            // ✅ Journalisation
            journalService.journaliserCreationAnnee(acteur, saved.getId(), saved.getNom());

            return saved;

        } catch (ANNEEACDEMIQUEEXISTEXCEPTION e) {
            journalService.journaliserEchec(acteur, TypeAction.ANNEE_ACADEMIQUE_CREEE,
                    "Annee_academique", null, e.getMessage());
            throw e;
        } catch (Exception e) {
            journalService.journaliserEchec(acteur, TypeAction.ANNEE_ACADEMIQUE_CREEE,
                    "Annee_academique", null, e.getMessage());
            throw new RuntimeException("Erreur lors de la création de l'année académique", e);
        }
    }

    @Override
    @Transactional
    public Annee_academique modifier(Long id, String nom, LocalDate dateDebut, LocalDate dateFin, boolean active, Utilisateur acteur) {
        try {
            Annee_academique annee = findById(id);

            if (dateDebut.isAfter(dateFin)) {
                throw new ANNEEACDEMIQUEEXISTEXCEPTION("La date de début doit être antérieure à la date de fin");
            }

            boolean overlap = anneeRepo.findAll().stream()
                    .filter(a -> !a.getId().equals(id))
                    .anyMatch(a -> (dateDebut.isBefore(a.getDateFin()) && dateFin.isAfter(a.getDateDebut())));

            if (overlap) {
                throw new ANNEEACDEMIQUEEXISTEXCEPTION("Les dates chevauchent une année académique existante");
            }

            annee.setNom(nom);
            annee.setDateDebut(dateDebut);
            annee.setDateFin(dateFin);
            annee.setActive(active);

            Annee_academique saved = anneeRepo.save(annee);

            // ✅ Journalisation
            journalService.journaliserModificationAnnee(acteur, saved.getId(), saved.getNom());

            return saved;

        } catch (ANNEEACDEMIQUEEXISTEXCEPTION e) {
            journalService.journaliserEchec(acteur, TypeAction.ANNEE_ACADEMIQUE_MODIFIEE,
                    "Annee_academique", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            journalService.journaliserEchec(acteur, TypeAction.ANNEE_ACADEMIQUE_MODIFIEE,
                    "Annee_academique", id, e.getMessage());
            throw new RuntimeException("Erreur lors de la modification de l'année académique", e);
        }
    }

    @Override
    @Transactional
    public Annee_academique activer(Long anneeId, Utilisateur acteur) {
        try {
            Annee_academique nouvelleAnnee = findById(anneeId);

            anneeRepo.findByActiveTrue().ifPresent(ancienne -> {
                semestreRepo.findByAnneeAcademiqueIdAndActifTrue(ancienne.getId())
                        .ifPresent(semestre -> {
                            semestre.setActif(false);
                            semestreRepo.save(semestre);
                        });
                ancienne.setActive(false);
                anneeRepo.save(ancienne);
            });

            nouvelleAnnee.setActive(true);
            Annee_academique saved = anneeRepo.save(nouvelleAnnee);

            // ✅ Journalisation
            journalService.journaliserActivationAnnee(acteur, saved.getId(), saved.getNom());

            return saved;

        } catch (Exception e) {
            journalService.journaliserEchec(acteur, TypeAction.ANNEE_ACADEMIQUE_ACTIVEE,
                    "Annee_academique", anneeId, e.getMessage());
            throw new RuntimeException("Erreur lors de l'activation de l'année académique", e);
        }
    }

    @Override
    public Annee_academique getAnneeActive() {
        return anneeRepo.findByActiveTrue()
                .orElseThrow(() -> new ANNEACADEMIQUEACTIVER("Aucune année académique active"));
    }

    @Override
    public List<Annee_academique> getAll() {
        return anneeRepo.findAllByOrderByNomDesc();
    }

    @Override
    public Annee_academique findById(Long id) {
        return anneeRepo.findById(id)
                .orElseThrow(() -> new ANNEACADEMIQUENOTFOUND("Année académique introuvable"));
    }

    @Override
    @Transactional
    public void supprimer(Long anneeId, Utilisateur acteur) {
        try {
            Annee_academique annee = findById(anneeId);
            if (annee.isActive()) {
                throw new IMPOSSIBLLEDESUPRIMERANNEEACADEMIQU("Impossible de supprimer l'année en cours");
            }
            anneeRepo.delete(annee);

            // ✅ Journalisation
            journalService.journaliserSuppressionAnnee(acteur, anneeId, annee.getNom());

        } catch (IMPOSSIBLLEDESUPRIMERANNEEACADEMIQU e) {
            journalService.journaliserEchec(acteur, TypeAction.ANNEE_ACADEMIQUE_SUPPRIMEE,
                    "Annee_academique", anneeId, e.getMessage());
            throw e;
        } catch (Exception e) {
            journalService.journaliserEchec(acteur, TypeAction.ANNEE_ACADEMIQUE_SUPPRIMEE,
                    "Annee_academique", anneeId, e.getMessage());
            throw new RuntimeException("Erreur lors de la suppression de l'année académique", e);
        }
    }
}