package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Annee_academique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Classe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.InstitutContexteActif;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Repository.InstitutContexteActifRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple.AnneeAcademiqueService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple.ClassesService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple.InstitutSecurityService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.inscription.DecisionHistory;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Etudiant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Inscription;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.JournalAction;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Enum.DecisionFinAnnee;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.StatutInscription;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.TypeAction;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.InscriptionRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService.IJournalActionService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService.InterfaceInscription;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InscriptionService implements InterfaceInscription {

    private final InscriptionRepository inscriptionRepo;
    private final EtudiantService etudiantService;
    private final ClassesService classesService;
    private final AnneeAcademiqueService anneeService;
    private final InstitutContexteActifRepository contexteRepo;
    private final IJournalActionService journalService;
    private final InstitutSecurityService securityService;

    // ═══════════════════════════════════════════════════════════
    // CRÉATION & MODIFICATION
    // ═══════════════════════════════════════════════════════════

    @Override
    @Transactional
    public Inscription inscrire(Long etudiantId, Long classeId, Long anneeId) {
        Etudiant etudiant = etudiantService.findById(etudiantId);
        Classe classe = classesService.findById(classeId);
        Annee_academique annee = anneeService.findById(anneeId);

        if (inscriptionRepo.existsByEtudiantIdAndAnneeAcademiqueId(etudiantId, anneeId)) {
            throw new RuntimeException("L'étudiant est déjà inscrit pour cette année académique.");
        }

        Long institutId = getInstitutIdFromClasse(classe);
        if (!securityService.canManageInstitut(getCurrentUser(), institutId)) {
            throw new AccessDeniedException("Vous n'avez pas les droits sur cet institut.");
        }

        Inscription inscription = Inscription.builder()
                .etudiant(etudiant).classe(classe).anneeAcademique(annee)
                .statut(StatutInscription.ACTIF).build();

        Inscription saved = inscriptionRepo.save(inscription);
        journaliser(TypeAction.INSCRIPTION_CREEE, "Inscription", saved.getId(),
                "Inscription créée pour " + etudiant.getMatricule());
        return saved;
    }

    @Override
    @Transactional
    public Inscription changerStatut(Long inscriptionId, StatutInscription statut) {
        Inscription inscription = findById(inscriptionId);
        inscription.setStatut(statut);
        Inscription saved = inscriptionRepo.save(inscription);
        journaliser(TypeAction.INSCRIPTION_MODIFIEE, "Inscription", saved.getId(),
                "Statut modifié : " + statut);
        return saved;
    }

    @Override
    @Transactional
    public Inscription enregistrerDecision(Long inscriptionId, String decisionStr) {
        if (decisionStr == null || decisionStr.trim().isEmpty()) {
            throw new IllegalArgumentException("La décision ne peut pas être vide");
        }

        // 1. Parsing sécurisé du String vers l'enum
        DecisionFinAnnee decision;
        try {
            decision = DecisionFinAnnee.valueOf(decisionStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Décision invalide. Valeurs acceptées : "
                    + Arrays.toString(DecisionFinAnnee.values()));
        }

        // 2. Récupération de l'utilisateur connecté
        Utilisateur acteur = getCurrentUser();

        // 3. Délégation à la méthode métier (qui gère déjà : sécurité, contexte actif, journalisation)
        enregistrerDecision(inscriptionId, decision, null, acteur);

        // 4. Retour de l'entité mise à jour
        return inscriptionRepo.findById(inscriptionId)
                .orElseThrow(() -> new RuntimeException("Inscription introuvable après mise à jour"));
    }

    @Transactional
    @Override
    public Inscription enregistrerDecison(Long inscriptionId, String decisionStr) {
        DecisionFinAnnee decision = DecisionFinAnnee.valueOf(decisionStr.toUpperCase());
        enregistrerDecision(inscriptionId, decision, null, getCurrentUser());
        return findById(inscriptionId);
    }

    @Transactional
    public void enregistrerDecision(Long inscriptionId, DecisionFinAnnee decision, String observations, Utilisateur acteur) {
        Inscription inscription = findById(inscriptionId);

        // ✅ Vérification via InstitutContexteActif (source de vérité)
        Long institutId = getInstitutIdFromInscription(inscription);
        if (!securityService.canManageInstitut(acteur, institutId)) {
            throw new AccessDeniedException("Accès refusé à cet institut.");
        }

        InstitutContexteActif contexte = contexteRepo.findByInstitutId(institutId).orElse(null);
        if (contexte != null && !inscription.getAnneeAcademique().getId().equals(contexte.getAnneeAcademique().getId())) {
            throw new RuntimeException("Impossible de modifier une décision d'une année fermée.");
        }

        inscription.setDecisionFinAnnee(decision);
        inscription.setDateDecision(LocalDate.now());
        inscription.setObservations(observations);

        if (decision == DecisionFinAnnee.EXCLU || decision == DecisionFinAnnee.ABANDON) {
            inscription.getEtudiant().setActive(false);
        }

        inscriptionRepo.save(inscription);
        journaliser(TypeAction.INSCRIPTION_MODIFIEE, "Inscription", inscriptionId,
                "Décision : " + decision + (observations != null ? " | " + observations : ""));
    }

    // ═══════════════════════════════════════════════════════════
    // RECHERCHE & LISTES
    // ═══════════════════════════════════════════════════════════

    @Override
    public Inscription findById(Long id) {
        Inscription inscription = inscriptionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Inscription introuvable"));

        Long institutId = getInstitutIdFromInscription(inscription);
        if (!securityService.canAccessInstitut(institutId)) {
            throw new AccessDeniedException("Accès refusé.");
        }
        return inscription;
    }

    @Override
    public List<Inscription> getActifsByClasseAndAnnee(Long classeId, Long anneeId) {
        return inscriptionRepo.findByClasseIdAndAnneeAcademiqueIdAndStatut(classeId, anneeId, StatutInscription.ACTIF);
    }

    public List<Inscription> getHistoriqueEtudiant(Long etudiantId) {
        Etudiant etudiant = etudiantService.findById(etudiantId);
        if (!securityService.canAccessInstitut(etudiant.getInstitut().getId())) {
            throw new AccessDeniedException("Accès refusé.");
        }
        return inscriptionRepo.findByEtudiantIdOrderByAnneeAcademiqueNomDesc(etudiantId);
    }

    public List<Inscription> getByClasseAndAnnee(Long classeId, Long anneeId) {
        if (classeId == null) {
            Long institutCible = securityService.getInstitutIdCourant();
            return anneeId != null && institutCible != null
                    ? inscriptionRepo.findByAnneeAcademiqueIdAndInstitutId(anneeId, institutCible)
                    : inscriptionRepo.findAll();
        }

        Classe classe = classesService.findById(classeId);
        if (!securityService.canAccessInstitut(getInstitutIdFromClasse(classe))) {
            throw new AccessDeniedException("Accès refusé.");
        }
        return inscriptionRepo.findByClasseIdAndAnneeAcademiqueId(classeId, anneeId);
    }

    public List<Inscription> getEtudiantsActifsByClasse(Long classeId, Long anneeId) {
        Classe classe = classesService.findById(classeId);
        if (!securityService.canAccessInstitut(getInstitutIdFromClasse(classe))) {
            throw new AccessDeniedException("Accès refusé.");
        }
        return inscriptionRepo.findByClasseIdAndAnneeAcademiqueIdAndStatut(classeId, anneeId, StatutInscription.ACTIF);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Inscription> getByClasseAndAnneePaginated(Long classeId, Long anneeId, Pageable pageable) {
        if (anneeId == null) throw new IllegalArgumentException("Année obligatoire");
        Long institutCible = securityService.getInstitutIdCourant();

        if (classeId == null) {
            return (institutCible == null)
                    ? inscriptionRepo.findByAnneeAcademiqueIdPaginated(anneeId, pageable)
                    : inscriptionRepo.findByAnneeAcademiqueIdAndInstitutIdPaginated(anneeId, institutCible, pageable);
        }

        Classe classe = classesService.findById(classeId);
        Long classeInstitutId = getInstitutIdFromClasse(classe);
        if (!securityService.canAccessInstitut(classeInstitutId) || (institutCible != null && !institutCible.equals(classeInstitutId))) {
            throw new AccessDeniedException("Accès refusé ou classe hors institut.");
        }
        return inscriptionRepo.findByClasseIdAndAnneeAcademiqueIdPaginated(classeId, anneeId, pageable);
    }

    // ═══════════════════════════════════════════════════════════
    // HISTORIQUE DÉCISIONS (Audit)
    // ═══════════════════════════════════════════════════════════

    @Override
    @Transactional(readOnly = true)
    public List<DecisionHistory> getDecisionHistory(Long inscriptionId) {
        findById(inscriptionId); // Sécurité
        List<JournalAction> logs = journalService.findByEntiteConcerneeAndEntiteId("Inscription", inscriptionId);

        if (logs == null || logs.isEmpty()) {
            return (List<DecisionHistory>) inscriptionRepo.findById(inscriptionId)
                    .map(ins -> ins.getDecisionFinAnnee() != null ? List.of(DecisionHistory.builder()
                                                                            .decision(ins.getDecisionFinAnnee())
                                                                            .date(ins.getDateDecision() != null ? ins.getDateDecision().atStartOfDay() : LocalDateTime.now())
                                                                            .observations(ins.getObservations())
                                                                            .acteur("Administration")
                                                                            .build()) : Collections.emptyList())
                    .orElse(Collections.emptyList());
        }

        return logs.stream()
                .map(log -> DecisionHistory.builder()
                        .decision(parseDecisionFromLog(log))
                        .date(log.getDateAction())
                        .observations(log.getDescription())
                        .acteur(log.getUtilisateur() != null ? log.getUtilisateur().getNom() + " " + log.getUtilisateur().getPrenom() : "Système")
                        .build())
                .sorted(Comparator.comparing(DecisionHistory::getDate).reversed())
                .toList();
    }

    // ═══════════════════════════════════════════════════════════
    // UTILITAIRES INTERNES
    // ═══════════════════════════════════════════════════════════

    private Long getInstitutIdFromInscription(Inscription ins) {
        return getInstitutIdFromClasse(ins.getClasse());
    }

    private Long getInstitutIdFromClasse(Classe classe) {
        if (classe == null || classe.getNiveau() == null || classe.getNiveau().getFiliere() == null
                || classe.getNiveau().getFiliere().getEcole() == null || classe.getNiveau().getFiliere().getEcole().getInstitut() == null) {
            throw new RuntimeException("Chemin académique incomplet pour déterminer l'institut.");
        }
        return classe.getNiveau().getFiliere().getEcole().getInstitut().getId();
    }

    private void journaliser(TypeAction type, String entite, Long id, String desc) {
        journalService.journaliserSucces(getCurrentUser(), type, entite, id, desc);
    }

    private Utilisateur getCurrentUser() {
        return (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private DecisionFinAnnee parseDecisionFromLog(JournalAction log) {
        try { return DecisionFinAnnee.valueOf(log.getTypeAction().name()); }
        catch (IllegalArgumentException e) { return null; }
    }
}