package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Annee_academique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Institut;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.TypeAction;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exception.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Repository.AnneeAcademiqueRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Repository.InstitutRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Repository.SemestreRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.InterfaceService.IAnneeAcademiqueService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService.IJournalActionService;

import java.time.LocalDate;
import java.util.List;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Semestre;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Exception.ANNEACADEMIQUEACTIVER;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Exception.ANNEACADEMIQUENOTFOUND;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Exception.ANNEEACDEMIQUEEXISTEXCEPTION;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Exception.IMPOSSIBLLEDESUPRIMERANNEEACADEMIQU;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Config.Security;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnneeAcademiqueService implements IAnneeAcademiqueService {

    private final AnneeAcademiqueRepository anneeRepo;
    private final SemestreRepository semestreRepo;
    private final InstitutRepository institutRepo;
    private final IJournalActionService journalService;
    private final InstitutSecurityService securityService;

    // ═══════════════════════════════════════════════════════════
    // CRÉATION
    // ═══════════════════════════════════════════════════════════

    @Override
    @Transactional
    public Annee_academique creer(String nom, LocalDate dateDebut, LocalDate dateFin,
                                  boolean active, Utilisateur acteur) {
        throw new UnsupportedOperationException("Utilisez creerAvecInstitut() avec un institutId");
    }

    @Transactional
    public void creerAvecInstitut(String nom, LocalDate dateDebut, LocalDate dateFin,
                                  boolean active, Long institutId, Utilisateur acteur) {
        try {
            if (!securityService.canManageInstitut(acteur, institutId)) {
                throw new AccessDeniedException("Vous n'avez pas les droits sur cet institut");
            }

            Institut institut = institutRepo.findById(institutId)
                    .orElseThrow(() -> new RuntimeException("Institut introuvable"));

            if (anneeRepo.existsByNomAndInstitutId(nom, institutId)) {
                throw new ANNEEACDEMIQUEEXISTEXCEPTION("L'année académique " + nom + " existe déjà pour cet institut");
            }

            if (dateDebut.isAfter(dateFin)) {
                throw new ANNEEACDEMIQUEEXISTEXCEPTION("La date de début doit être antérieure à la date de fin");
            }

            List<Annee_academique> anneesInstitut = anneeRepo.findByInstitutId(institutId);
            boolean overlap = anneesInstitut.stream().anyMatch(annee ->
                    (dateDebut.isBefore(annee.getDateFin()) && dateFin.isAfter(annee.getDateDebut()))
            );

            if (overlap) {
                throw new ANNEEACDEMIQUEEXISTEXCEPTION("Les dates chevauchent une année académique existante pour cet institut");
            }

            Annee_academique annee = new Annee_academique();
            annee.setNom(nom);
            annee.setDateDebut(dateDebut);
            annee.setDateFin(dateFin);
            annee.setActive(active);
            annee.setInstitut(institut);

            Annee_academique saved = anneeRepo.save(annee);

            // ✅ Journalisation
            journalService.journaliserCreationAnnee(acteur, saved.getId(), saved.getNom());

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

    // ═══════════════════════════════════════════════════════════
    // MODIFICATION
    // ═══════════════════════════════════════════════════════════

    @Override
    @Transactional
    public Annee_academique modifier(Long id, String nom, LocalDate dateDebut, LocalDate dateFin,
                                     boolean active, Utilisateur acteur) {
        try {
            Annee_academique annee = findById(id);

            if (!securityService.canManageInstitut(acteur, annee.getInstitut().getId())) {
                throw new AccessDeniedException("Vous n'avez pas les droits sur cet institut");
            }

            if (dateDebut.isAfter(dateFin)) {
                throw new ANNEEACDEMIQUEEXISTEXCEPTION("La date de début doit être antérieure à la date de fin");
            }

            List<Annee_academique> anneesInstitut = anneeRepo.findByInstitutId(annee.getInstitut().getId());
            boolean overlap = anneesInstitut.stream()
                    .filter(a -> !a.getId().equals(id))
                    .anyMatch(a -> (dateDebut.isBefore(a.getDateFin()) && dateFin.isAfter(a.getDateDebut())));

            if (overlap) {
                throw new ANNEEACDEMIQUEEXISTEXCEPTION("Les dates chevauchent une année académique existante pour cet institut");
            }

            annee.setNom(nom);
            annee.setDateDebut(dateDebut);
            annee.setDateFin(dateFin);
            annee.setActive(active);

            Annee_academique saved = anneeRepo.save(annee);

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

    // ═══════════════════════════════════════════════════════════
    // ACTIVATION
    // ═══════════════════════════════════════════════════════════

    @Override
    @Transactional
    public Annee_academique activer(Long anneeId, Utilisateur acteur) {
        try {
            Annee_academique nouvelleAnnee = findById(anneeId);
            Long institutId = nouvelleAnnee.getInstitut().getId();

            if (!securityService.canManageInstitut(acteur, institutId)) {
                throw new AccessDeniedException("Vous n'avez pas les droits sur cet institut");
            }

            anneeRepo.findByInstitutIdAndActiveTrue(institutId).ifPresent(ancienne -> {
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

    @Transactional
    @Override
    public Annee_academique desactiver(Long anneeId, Utilisateur acteur) {
        try {
            Annee_academique anneeADesactiver = findById(anneeId);
            Long institutId = anneeADesactiver.getInstitut().getId();

            if (!securityService.canManageInstitut(acteur, institutId)) {
                throw new AccessDeniedException("Vous n'avez pas les droits sur cet institut");
            }

            anneeADesactiver.setActive(false);

            semestreRepo.findByAnneeAcademiqueIdAndActifTrue(anneeId)
                    .ifPresent(semestre -> {
                        semestre.setActif(false);
                        semestreRepo.save(semestre);
                    });

            Annee_academique saved = anneeRepo.save(anneeADesactiver);
            journalService.journaliserDesactivationAnnee(acteur, saved.getId(), saved.getNom());

            return saved;

        } catch (AccessDeniedException e) {
            // Laisser remonter sans journaliser dans une transaction mourante
            throw e;
        } catch (Exception e) {
            journalService.journaliserEchec(acteur, TypeAction.ANNEE_ACADEMIQUE_DESACTIVEE,
                    "Annee_academique", anneeId, e.getMessage());
            throw new RuntimeException("Erreur lors de la désactivation de l'année académique", e);
        }
    }
    // ═══════════════════════════════════════════════════════════
    // RÉCUPÉRATION
    // ═══════════════════════════════════════════════════════════

    @Override
    public Annee_academique getAnneeActive() {
        Long institutId = securityService.getInstitutIdCourant();
        if (institutId == null) {
            throw new RuntimeException("Veuillez sélectionner un institut");
        }
        return anneeRepo.findByInstitutIdAndActiveTrue(institutId)
                .orElseThrow(() -> new ANNEACADEMIQUEACTIVER("Aucune année académique active pour cet institut"));
    }

    @Override
    public List<Annee_academique> getAll() {
        Long institutId = securityService.getInstitutIdCourant();
        if (institutId == null) {
            return anneeRepo.findAllByOrderByNomDesc();
        }
        return anneeRepo.findByInstitutIdOrderByNomDesc(institutId);
    }

    @Override
    public Annee_academique findById(Long id) {
        Annee_academique annee = anneeRepo.findById(id)
                .orElseThrow(() -> new ANNEACADEMIQUENOTFOUND("Année académique introuvable"));

        if (!securityService.canAccessInstitut(annee.getInstitut().getId())) {
            throw new AccessDeniedException("Vous n'avez pas accès à cette année académique");
        }

        return annee;
    }

    @Override
    public Annee_academique findEntityById(Long id) {
        return findById(id);
    }

    // ═══════════════════════════════════════════════════════════
    // SUPPRESSION
    // ═══════════════════════════════════════════════════════════

    @Override
    @Transactional
    public void supprimer(Long anneeId, Utilisateur acteur) {
        try {
            Annee_academique annee = findById(anneeId);

            if (!securityService.canManageInstitut(acteur, annee.getInstitut().getId())) {
                throw new AccessDeniedException("Vous n'avez pas les droits sur cet institut");
            }

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

    // ═══════════════════════════════════════════════════════════
    // MÉTHODES MULTI-INSTITUTS
    // ═══════════════════════════════════════════════════════════

    public List<Annee_academique> getByInstitut(Long institutId) {
        if (!securityService.canAccessInstitut(institutId)) {
            throw new AccessDeniedException("Vous n'avez pas accès à cet institut");
        }
        return anneeRepo.findByInstitutIdOrderByNomDesc(institutId);
    }

    public Annee_academique getAnneeActivePourInstitut(Long institutId) {
        if (!securityService.canAccessInstitut(institutId)) {
            throw new AccessDeniedException("Vous n'avez pas accès à cet institut");
        }
        return anneeRepo.findByInstitutIdAndActiveTrue(institutId)
                .orElseThrow(() -> new ANNEACADEMIQUEACTIVER("Aucune année académique active pour cet institut"));
    }
}