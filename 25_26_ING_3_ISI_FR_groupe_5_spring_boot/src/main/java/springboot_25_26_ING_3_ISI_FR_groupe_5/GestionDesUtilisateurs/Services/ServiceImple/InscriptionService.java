package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Annee_academique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Classe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Etudiant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Inscription;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Enum.DecisionFinAnnee;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.StatutInscription;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.TypeAction;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.InscriptionRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService.IJournalActionService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService.InterfaceInscription;

import java.time.LocalDate;
import java.util.List;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Institut;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple.AnneeAcademiqueService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple.ClassesService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple.InstitutSecurityService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Config.Security;

@Service
@RequiredArgsConstructor
public class InscriptionService implements InterfaceInscription {

    private final InscriptionRepository inscriptionRepo;
    private final EtudiantService etudiantService;
    private final ClassesService classesService;
    private final AnneeAcademiqueService anneeService;
    private final IJournalActionService journalService;
    private final InstitutSecurityService securityService;

    // ═══════════════════════════════════════════════════════════
    // ENREGISTRER UNE DÉCISION
    // ═══════════════════════════════════════════════════════════

    @Transactional
    public void enregistrerDecision(Long inscriptionId, DecisionFinAnnee decision,
                                    String observations, Utilisateur acteur) {

        Inscription inscription = findById(inscriptionId);

        // 🆕 Vérifier l'accès à l'institut
        Long institutId = getInstitutIdFromInscription(inscription);
        if (!securityService.canManageInstitut(acteur, institutId)) {
            throw new AccessDeniedException("Vous n'avez pas les droits sur cet institut");
        }

        // Vérifier qu'on est dans l'année active DE CET INSTITUT
        Annee_academique anneeActive = anneeService.getAnneeActivePourInstitut(institutId);
        if (!inscription.getAnneeAcademique().getId().equals(anneeActive.getId())) {
            throw new RuntimeException("Impossible de modifier une décision d'une année passée");
        }

        inscription.setDecisionFinAnnee(decision);
        inscription.setDateDecision(LocalDate.now());
        inscription.setObservations(observations);

        // Si exclu → désactiver l'étudiant
        if (decision == DecisionFinAnnee.EXCLU) {
            inscription.getEtudiant().setActive(false);
        }

        inscriptionRepo.save(inscription);

        // Journalisation
        journalService.journaliserSucces(acteur, TypeAction.INSCRIPTION_MODIFIEE,
                "Inscription", inscriptionId,
                "Décision enregistrée : " + decision + (observations != null ? " - " + observations : ""));
    }

    // ═══════════════════════════════════════════════════════════
    // RECHERCHE
    // ═══════════════════════════════════════════════════════════

    public Inscription findById(Long id) {
        Inscription inscription = inscriptionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Inscription introuvable"));

        // 🆕 Vérifier l'accès
        Long institutId = getInstitutIdFromInscription(inscription);
        if (!securityService.canAccessInstitut(institutId)) {
            throw new AccessDeniedException("Vous n'avez pas accès à cette inscription");
        }

        return inscription;
    }

    // ═══════════════════════════════════════════════════════════
    // LISTES FILTRÉES
    // ═══════════════════════════════════════════════════════════

    public List<Inscription> getHistoriqueEtudiant(Long etudiantId) {
        Etudiant etudiant = etudiantService.findById(etudiantId);

        // 🆕 Vérifier l'accès à l'étudiant
        if (!securityService.canAccessInstitut(etudiant.getInstitut().getId())) {
            throw new AccessDeniedException("Vous n'avez pas accès à cet étudiant");
        }

        return inscriptionRepo.findByEtudiantIdOrderByAnneeAcademiqueNomDesc(etudiantId);
    }

    @Override
    public Inscription inscrire(Long etudiantId, Long classeId, Long anneeId) {
        return null;
    }

    @Override
    public Inscription changerStatut(Long inscriptionId, StatutInscription statut) {
        return null;
    }

    @Override
    public Inscription enregistrerDecision(Long inscriptionId, String decision) {
        return null;
    }

    public List<Inscription> getByClasseAndAnnee(Long classeId, Long anneeId) {
        // Si classeId est null, on filtre par institut
        if (classeId == null) {
            Long institutId = securityService.getInstitutIdCourant();
            if (institutId == null) {
                // Super Admin : tout voir
                return anneeId != null
                        ? inscriptionRepo.findByAnneeAcademiqueId(anneeId)
                        : inscriptionRepo.findAll();
            }
            // Admin Institut : filtrer par son institut
            return inscriptionRepo.findByAnneeAcademiqueIdAndInstitutId(anneeId, institutId);
        }

        // Vérifier l'accès à la classe
        Classe classe = classesService.findById(classeId);
        Long institutId = getInstitutIdFromClasse(classe);
        if (!securityService.canAccessInstitut(institutId)) {
            throw new AccessDeniedException("Vous n'avez pas accès à cette classe");
        }

        return inscriptionRepo.findByClasseIdAndAnneeAcademiqueId(classeId, anneeId);
    }




    @Override
    public List<Inscription> getActifsByClasseAndAnnee(Long classeId, Long anneeId) {
        return List.of();
    }

    private Long getInstitutIdFromClasse(Classe classe) {
        if (classe.getNiveau() != null
                && classe.getNiveau().getFiliere() != null
                && classe.getNiveau().getFiliere().getEcole() != null
                && classe.getNiveau().getFiliere().getEcole().getInstitut() != null) {
            return classe.getNiveau().getFiliere().getEcole().getInstitut().getId();
        }
        throw new RuntimeException("Impossible de déterminer l'institut de la classe");
    }

    public List<Inscription> getEtudiantsActifsByClasse(Long classeId, Long anneeId) {
        Classe classe = classesService.findById(classeId);

        // 🆕 Vérifier l'accès
        Long institutId = getInstitutIdFromClasse(classe);
        if (!securityService.canAccessInstitut(institutId)) {
            throw new AccessDeniedException("Vous n'avez pas accès à cette classe");
        }

        return inscriptionRepo.findByClasseIdAndAnneeAcademiqueIdAndStatut(classeId, anneeId, StatutInscription.ACTIF);
    }

    // ═══════════════════════════════════════════════════════════
    // MÉTHODES UTILITAIRES
    // ═══════════════════════════════════════════════════════════

    private Long getInstitutIdFromInscription(Inscription inscription) {
        if (inscription.getClasse() != null
                && inscription.getClasse().getNiveau() != null
                && inscription.getClasse().getNiveau().getFiliere() != null
                && inscription.getClasse().getNiveau().getFiliere().getEcole() != null
                && inscription.getClasse().getNiveau().getFiliere().getEcole().getInstitut() != null) {
            return inscription.getClasse().getNiveau().getFiliere().getEcole().getInstitut().getId();
        }
        throw new RuntimeException("Impossible de déterminer l'institut de l'inscription");
    }


}