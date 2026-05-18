package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.DTO.Migration.MigrationResultat;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Enum.DecisionFinAnnee;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Enum.MigrationBatchStatus;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Enum.MigrationDecisionStatus;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Enum.MigrationSourceType;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Repository.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.InterfaceService.IMigrationService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Services.ServiceImple.ProgrammationUEService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.AssistantPedagogique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Etudiant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Inscription;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.StatutInscription;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.AssistantRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.EnseignantRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.EtudiantRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.InscriptionRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService.IJournalActionService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.TypeAction;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MigrationService implements IMigrationService {

    private static final int PAGINATION_BATCH_SIZE = 200;

    private final InscriptionRepository inscriptionRepo;
    private final SemestreService semestreService;
    private final EtudiantRepository etudiantRepository;
    private final EnseignantRepository enseignantRepository;
    private final AssistantRepository assistantRepository;
    private final AnneeAcademiqueService anneeService;
    private final NiveauService niveauService;
    private final ClassesRepository classesRepo;
    private final UERepository ueRepository;
    private final FiliereRepository filiereRepository;
    private final SpecialiteRepository specialiteRepository;
    private final EcoleRepository ecoleRepository;
    private final InstitutRepository institutRepository;
    private final ProgrammationUEService programmationService;
    private final IJournalActionService journalService;
    private final InstitutSecurityService securityService;
    private final MigrationBatchRepository batchRepo;
    private final MigrationDecisionRepository decisionRepo;
    private final InstitutContexteActifRepository contexteRepo;

    // ═══════════════════════════════════════════════════════════
    // 1️⃣ MIGRATION COMPLÈTE (tout l'institut)
    // ═══════════════════════════════════════════════════════════

    @Override
    @Transactional
    public MigrationResultat migrer(Long nouvelleAnneeId, Utilisateur acteur) {
        Long institutId = securityService.getInstitutIdCourantObligatoire();
        return migrerPourInstitut(institutId, nouvelleAnneeId, acteur);
    }

    @Override
    @Transactional
    public MigrationResultat migrerPourInstitut(Long institutId, Long nouvelleAnneeId, Utilisateur acteur) {
        MigrationContext ctx = prepareMigrationContext(institutId, nouvelleAnneeId, acteur);
        MigrationBatch batch = initierBatch(ctx, acteur);

        log.info("🚀 Migration complète institut {} : {} → {}",
                institutId, ctx.ancienne().getNom(), ctx.nouvelle().getNom());

        MigrationResultat resultat = new MigrationResultat();
        int page = 0;
        Page<Inscription> batchPage;
        do {
            Pageable pageable = PageRequest.of(page++, PAGINATION_BATCH_SIZE);
            batchPage = inscriptionRepo.findByAnneeAcademiqueIdPaginated(ctx.ancienne().getId(), pageable);
            for (Inscription inscription : batchPage) {
                traiterInscriptionPourMigration(inscription, ctx.nouvelle(), batch, resultat);
            }
            decisionRepo.flush();
        } while (batchPage.hasNext());

        // Assistants pédagogiques — réaffecter mêmes classes
        migrerTousLesAssistants(institutId, ctx.nouvelle(), batch, resultat);

        programmationService.dupliquerVersNouvelleAnnee(ctx.ancienne().getId(), ctx.nouvelle().getId());
        publierContexteActif(ctx.institutId(), ctx.nouvelle());

        batch.setStatus(MigrationBatchStatus.TERMINEE);
        batch.setRollbackPossible(true);
        batchRepo.save(batch);

        journalService.journaliserSucces(acteur, TypeAction.MIGRATION_EXECUTE, "Migration", batch.getId(),
                buildDescription(ctx.ancienne(), ctx.nouvelle(), resultat));

        log.info("✅ Migration complète terminée : {}", resultat);
        return resultat;
    }

    // ═══════════════════════════════════════════════════════════
    // 2️⃣ MIGRATIONS SÉLECTIVES MULTI-SÉLECTION
    // ═══════════════════════════════════════════════════════════

    // ── Institut(s) ────────────────────────────────────────────

    /**
     * Migre un ou plusieurs instituts.
     * Descend : Institut → Écoles → Filières → Niveaux → Classes → Inscriptions
     */
    @Transactional
    @Override
    public MigrationResultat migrerInstituts(List<Long> institutIds, Long nouvelleAnneeId, Utilisateur acteur) {
        validerListe(institutIds, "instituts");
        MigrationResultat resultat = new MigrationResultat();

        for (Long institutId : institutIds) {
            Institut institut = institutRepository.findById(institutId)
                    .orElseThrow(() -> new MigrationException("Institut introuvable : " + institutId));

            MigrationContext ctx = prepareMigrationContext(institutId, nouvelleAnneeId, acteur);
            MigrationBatch batch = initierBatch(ctx, acteur);

            List<Classe> classes = classesRepo.findByInstitutId(institutId);
            migrerInscriptionsDesClasses(classes, ctx, batch, resultat);
            migrerTousLesAssistants(institutId, ctx.nouvelle(), batch, resultat);

            terminerBatch(batch);
            log.info("✅ Institut migré : {}", institut.getNom());
        }
        return resultat;
    }

    // ── École(s) ───────────────────────────────────────────────

    /**
     * Migre une ou plusieurs écoles.
     * Descend : École → Filières → Niveaux → Classes → Inscriptions
     */
    @Transactional
    @Override
    public MigrationResultat migrerEcoles(List<Long> ecoleIds, Long nouvelleAnneeId, Utilisateur acteur) {
        validerListe(ecoleIds, "écoles");
        MigrationResultat resultat = new MigrationResultat();

        for (Long ecoleId : ecoleIds) {
            Ecole ecole = ecoleRepository.findById(ecoleId)
                    .orElseThrow(() -> new MigrationException("École introuvable : " + ecoleId));

            Long institutId = ecole.getInstitut().getId();
            MigrationContext ctx = prepareMigrationContext(institutId, nouvelleAnneeId, acteur);
            MigrationBatch batch = initierBatch(ctx, acteur);

            List<Classe> classes = classesRepo.findByEcoleId(ecoleId);
            migrerInscriptionsDesClasses(classes, ctx, batch, resultat);

            terminerBatch(batch);
            log.info("✅ École migrée : {}", ecole.getNom());
        }
        return resultat;
    }

    // ── Filière(s) ─────────────────────────────────────────────

    /**
     * Migre une ou plusieurs filières.
     * Descend : Filière → Niveaux → Classes → Inscriptions
     */
    @Transactional
    @Override
    public MigrationResultat migrerFilieres(List<Long> filiereIds, Long nouvelleAnneeId, Utilisateur acteur) {
        validerListe(filiereIds, "filières");
        MigrationResultat resultat = new MigrationResultat();

        for (Long filiereId : filiereIds) {
            Filiere filiere = filiereRepository.findById(filiereId)
                    .orElseThrow(() -> new MigrationException("Filière introuvable : " + filiereId));

            Long institutId = Optional.ofNullable(filiere.getInstitutId())
                    .orElseThrow(() -> new MigrationException("Institut introuvable pour filière : " + filiereId));

            MigrationContext ctx = prepareMigrationContext(institutId, nouvelleAnneeId, acteur);
            MigrationBatch batch = initierBatch(ctx, acteur);

            List<Classe> classes = classesRepo.findByFiliereId(filiereId);
            migrerInscriptionsDesClasses(classes, ctx, batch, resultat);

            terminerBatch(batch);
            log.info("✅ Filière migrée : {}", filiere.getNom());
        }
        return resultat;
    }

    // ── Spécialité(s) ──────────────────────────────────────────

    /**
     * Migre une ou plusieurs spécialités.
     * Descend : Spécialité → Niveaux → Classes → Inscriptions
     */
    @Transactional
    @Override
    public MigrationResultat migrerSpecialites(List<Long> specialiteIds, Long nouvelleAnneeId, Utilisateur acteur) {
        validerListe(specialiteIds, "spécialités");
        MigrationResultat resultat = new MigrationResultat();

        for (Long specialiteId : specialiteIds) {
            Specialite specialite = specialiteRepository.findById(specialiteId)
                    .orElseThrow(() -> new MigrationException("Spécialité introuvable : " + specialiteId));

            Long institutId = Optional.ofNullable(specialite.getInstitutId())
                    .orElseThrow(() -> new MigrationException("Institut introuvable pour spécialité : " + specialiteId));

            MigrationContext ctx = prepareMigrationContext(institutId, nouvelleAnneeId, acteur);
            MigrationBatch batch = initierBatch(ctx, acteur);

            List<Classe> classes = classesRepo.findBySpecialiteId(specialiteId);
            migrerInscriptionsDesClasses(classes, ctx, batch, resultat);

            terminerBatch(batch);
            log.info("✅ Spécialité migrée : {}", specialite.getNom());
        }
        return resultat;
    }

    // ── Niveau(x) ──────────────────────────────────────────────

    /**
     * Migre un ou plusieurs niveaux.
     * Descend : Niveau → Classes → Inscriptions
     */
    @Transactional
    @Override
    public MigrationResultat migrerNiveaux(List<Long> niveauIds, Long nouvelleAnneeId, Utilisateur acteur) {
        validerListe(niveauIds, "niveaux");
        MigrationResultat resultat = new MigrationResultat();

        for (Long niveauId : niveauIds) {
            Niveau niveau = niveauService.findById(niveauId);

            Long institutId = Optional.ofNullable(niveau.getInstitutId())
                    .orElseThrow(() -> new MigrationException("Institut introuvable pour niveau : " + niveauId));

            MigrationContext ctx = prepareMigrationContext(institutId, nouvelleAnneeId, acteur);
            MigrationBatch batch = initierBatch(ctx, acteur);

            List<Classe> classes = classesRepo.findByNiveauId(niveauId);
            migrerInscriptionsDesClasses(classes, ctx, batch, resultat);

            terminerBatch(batch);
            log.info("✅ Niveau migré : {}", niveau.getNom());
        }
        return resultat;
    }

    // ── Classe(s) ──────────────────────────────────────────────

    /**
     * Migre une ou plusieurs classes.
     * Traite directement les inscriptions de chaque classe.
     */
    @Transactional
    @Override
    public MigrationResultat migrerClasses(List<Long> classeIds, Long nouvelleAnneeId, Utilisateur acteur) {
        validerListe(classeIds, "classes");
        MigrationResultat resultat = new MigrationResultat();

        // Regrouper les classes par institut pour créer un batch par institut
        List<Classe> classes = classeIds.stream()
                .map(id -> classesRepo.findById(id)
                        .orElseThrow(() -> new MigrationException("Classe introuvable : " + id)))
                .collect(Collectors.toList());

        // On prend l'institut de la première classe — toutes doivent appartenir au même
        Long institutId = getInstitutIdFromClasse(classes.get(0));
        MigrationContext ctx = prepareMigrationContext(institutId, nouvelleAnneeId, acteur);
        MigrationBatch batch = initierBatch(ctx, acteur);

        migrerInscriptionsDesClasses(classes, ctx, batch, resultat);
        terminerBatch(batch);

        log.info("✅ {} classe(s) migrée(s)", classes.size());
        return resultat;
    }

    // ── Étudiant(s) ────────────────────────────────────────────

    /**
     * Migre un ou plusieurs étudiants individuellement.
     */
    @Transactional
    @Override
    public MigrationResultat migrerEtudiants(List<Long> etudiantIds, Long nouvelleAnneeId, Utilisateur acteur) {
        validerListe(etudiantIds, "étudiants");
        MigrationResultat resultat = new MigrationResultat();

        for (Long etudiantId : etudiantIds) {
            Etudiant etudiant = etudiantRepository.findById(etudiantId)
                    .orElseThrow(() -> new MigrationException("Étudiant introuvable : " + etudiantId));

            MigrationContext ctx = prepareMigrationContext(
                    etudiant.getInstitut().getId(), nouvelleAnneeId, acteur);
            MigrationBatch batch = initierBatch(ctx, acteur);

            Inscription inscription = inscriptionRepo
                    .findByEtudiantIdAndAnneeAcademiqueId(etudiantId, ctx.ancienne().getId())
                    .orElseThrow(() -> new MigrationException(
                            "Inscription introuvable pour étudiant : " + etudiantId));

            traiterInscriptionPourMigration(inscription, ctx.nouvelle(), batch, resultat);
            terminerBatch(batch);
        }

        log.info("✅ {} étudiant(s) migré(s)", etudiantIds.size());
        return resultat;
    }

    // ── Enseignant(s) ──────────────────────────────────────────

    /**
     * Migre un ou plusieurs enseignants.
     * Duplique leurs programmations UE vers N+1.
     */
    @Transactional
    @Override
    public MigrationResultat migrerEnseignants(List<Long> enseignantIds, Long nouvelleAnneeId, Utilisateur acteur) {
        validerListe(enseignantIds, "enseignants");
        MigrationResultat resultat = new MigrationResultat();

        for (Long enseignantId : enseignantIds) {
            Enseignant enseignant = enseignantRepository.findById(enseignantId)
                    .orElseThrow(() -> new MigrationException("Enseignant introuvable : " + enseignantId));

            MigrationContext ctx = prepareMigrationContext(
                    enseignant.getInstitut().getId(), nouvelleAnneeId, acteur);
            MigrationBatch batch = initierBatch(ctx, acteur);

            programmationService.dupliquerEnseignantVersNouvelleAnnee(
                    enseignantId, ctx.ancienne().getId(), ctx.nouvelle().getId());

            decisionRepo.save(decisionBuilder(batch, MigrationSourceType.ENSEIGNANT,
                    enseignantId, enseignant.getNom(), MigrationDecisionStatus.MIGREE));

            resultat.ajouterEnseignant(enseignant.getEmail());
            terminerBatch(batch);
            log.info("✅ Enseignant migré : {}", enseignant.getNom());
        }
        return resultat;
    }

    // ── Assistant(s) pédagogique(s) ────────────────────────────

    /**
     * Migre un ou plusieurs assistants pédagogiques.
     * Réaffecte automatiquement les mêmes classes en N+1.
     * Les classes de N+1 existent déjà après migration des étudiants.
     */
    @Transactional
    @Override
    public MigrationResultat migrerAssistants(List<Long> assistantIds, Long nouvelleAnneeId, Utilisateur acteur) {
        validerListe(assistantIds, "assistants");
        MigrationResultat resultat = new MigrationResultat();

        for (Long assistantId : assistantIds) {
            AssistantPedagogique assistant = assistantRepository.findById(assistantId)
                    .orElseThrow(() -> new MigrationException("Assistant introuvable : " + assistantId));

            MigrationContext ctx = prepareMigrationContext(
                    assistant.getInstitut().getId(), nouvelleAnneeId, acteur);
            MigrationBatch batch = initierBatch(ctx, acteur);

            migrerAssistant(assistant, ctx.nouvelle(), batch, resultat);
            terminerBatch(batch);
        }

        log.info("✅ {} assistant(s) migré(s)", assistantIds.size());
        return resultat;
    }

    // ── UE(s) ──────────────────────────────────────────────────

    /**
     * Migre une ou plusieurs UE.
     * Duplique leurs programmations vers N+1.
     */
    @Transactional
    @Override
    public MigrationResultat migrerUEs(List<Long> ueIds, Long nouvelleAnneeId, Utilisateur acteur) {
        validerListe(ueIds, "UEs");
        MigrationResultat resultat = new MigrationResultat();

        for (Long ueId : ueIds) {
            UE ue = ueRepository.findById(ueId)
                    .orElseThrow(() -> new MigrationException("UE introuvable : " + ueId));

            Long institutId = getInstitutIdFromUE(ue);
            MigrationContext ctx = prepareMigrationContext(institutId, nouvelleAnneeId, acteur);
            MigrationBatch batch = initierBatch(ctx, acteur);

            programmationService.dupliquerUEVersNouvelleAnnee(
                    ueId, ctx.ancienne().getId(), ctx.nouvelle().getId());

            decisionRepo.save(decisionBuilder(batch, MigrationSourceType.UE,
                    ueId, ue.getCode(), MigrationDecisionStatus.MIGREE));

            resultat.ajouterUE(ue.getCode());
            terminerBatch(batch);
            log.info("✅ UE migrée : {}", ue.getCode());
        }
        return resultat;
    }

    // ═══════════════════════════════════════════════════════════
    // 3️⃣ ROLLBACK
    // ═══════════════════════════════════════════════════════════

    @Override
    @Transactional
    public void rollbackMigration(Long batchId, Utilisateur acteur) {
        MigrationBatch batch = batchRepo.findById(batchId)
                .orElseThrow(() -> new MigrationException("Batch introuvable : " + batchId));

        if (!batch.peutEtreRollback()) {
            throw new MigrationException(
                    "Ce batch ne peut plus être annulé. Statut : " + batch.getStatus() +
                            (batch.getStatus() == MigrationBatchStatus.PUBLIEE
                                    ? " — migration déjà publiée définitivement." : "."));
        }

        Annee_academique anneeSource = batch.getSourceAnnee();
        Annee_academique anneeTarget = batch.getTargetAnnee();

        log.info("↩️ Rollback batch {} : restauration état {} ← {} (données N+1 conservées)",
                batchId, anneeSource.getNom(), anneeTarget.getNom());

        List<MigrationDecision> decisions = decisionRepo.findByBatchId(batchId);

        for (MigrationDecision decision : decisions) {
            if (decision.getStatus() != MigrationDecisionStatus.MIGREE) continue;

            if (decision.getSourceType() == MigrationSourceType.INSCRIPTION) {
                Inscription inscriptionN = inscriptionRepo.findById(decision.getSourceId()).orElse(null);
                if (inscriptionN != null) {
                    Etudiant etudiant = inscriptionN.getEtudiant();
                    restaurerClasseEtudiant(etudiant, anneeSource);
                    if (!etudiant.isActive()) {
                        etudiant.setActive(true);
                        etudiantRepository.save(etudiant);
                    }
                }
            }
            // ENSEIGNANT, ASSISTANT, UE, PROGRAMMATION_UE, CLASSE : rien à faire
            // Les données N+1 sont conservées

            decision.setStatus(MigrationDecisionStatus.ROLLBACK);
            decisionRepo.save(decision);
        }

        // Restaurer le contexte actif vers l'année source
        restaurerContexteActif(batch.getInstitut().getId(), anneeSource);

        batch.setStatus(MigrationBatchStatus.ANNULE);
        batch.setRollbackPossible(false);
        batchRepo.save(batch);

        journalService.journaliserSucces(acteur, TypeAction.MIGRATION_ROLLBACK, "Migration", batchId,
                "Rollback " + anneeSource.getNom() + " ← " + anneeTarget.getNom()
                        + " (données N+1 conservées)");

        log.info("✅ Rollback terminé pour batch {}", batchId);
    }

    // ═══════════════════════════════════════════════════════════
    // 4️⃣ PUBLICATION DÉFINITIVE
    // ═══════════════════════════════════════════════════════════

    @Override
    @Transactional
    public void publierMigration(Long batchId, Utilisateur acteur) {
        MigrationBatch batch = batchRepo.findById(batchId)
                .orElseThrow(() -> new MigrationException("Batch introuvable : " + batchId));

        if (!batch.peutEtrePublie()) {
            throw new MigrationException(
                    "Ce batch ne peut pas être publié. Statut : " + batch.getStatus());
        }

        batch.validerPublication();
        batchRepo.save(batch);

        journalService.journaliserSucces(acteur, TypeAction.MIGRATION_PUBLIEE, "Migration", batchId,
                "Publication définitive " +
                        batch.getSourceAnnee().getNom() + " → " + batch.getTargetAnnee().getNom());

        log.info("📌 Migration {} publiée définitivement", batchId);
    }

    // ═══════════════════════════════════════════════════════════
    // 5️⃣ SIMULATION & VÉRIFICATION
    // ═══════════════════════════════════════════════════════════

    @Override
    @Transactional(readOnly = true)
    public MigrationResultat simuler(Long nouvelleAnneeId) {
        Long institutId = securityService.getInstitutIdCourantObligatoire();
        return simulerPourInstitut(institutId, nouvelleAnneeId);
    }

    @Override
    @Transactional(readOnly = true)
    public MigrationResultat simuler(Long institutId, Long nouvelleAnneeId) {
        Annee_academique ancienne = anneeService.getAnneeActivePourInstitut(institutId);
        Annee_academique nouvelle = anneeService.findEntityById(nouvelleAnneeId);

        if (nouvelle == null || nouvelle.isActive()) {
            throw new MigrationException("L'année cible n'existe pas ou est déjà active");
        }
        if (!nouvelle.getInstitut().getId().equals(institutId)) {
            throw new MigrationException("L'année cible n'appartient pas au même institut");
        }

        MigrationResultat resultat = new MigrationResultat();
        int page = 0;
        Page<Inscription> batchPage;

        do {
            Pageable pageable = PageRequest.of(page++, PAGINATION_BATCH_SIZE);
            batchPage = inscriptionRepo.findByAnneeAcademiqueIdPaginated(ancienne.getId(), pageable);

            batchPage.forEach(i -> {
                DecisionFinAnnee decision = i.getDecisionFinAnnee();
                if (decision == null) {
                    resultat.ajouterIgnore(i.getEtudiant().getMatricule());
                    return;
                }
                switch (decision) {
                    case ADMIS -> {
                        Optional<Niveau> niveauSup = niveauService.getNiveauSuperieur(
                                i.getClasse().getNiveau());
                        if (niveauSup.isEmpty()) {
                            resultat.ajouterDiplome(i.getEtudiant().getMatricule());
                        } else {
                            resultat.ajouterAdmis(i.getEtudiant().getMatricule());
                        }
                    }
                    case REDOUBLANT -> resultat.ajouterRedoublant(i.getEtudiant().getMatricule());
                    case EXCLU     -> resultat.ajouterExclu(i.getEtudiant().getMatricule());
                    case DIPLOME   -> resultat.ajouterDiplome(i.getEtudiant().getMatricule());
                }
            });
        } while (batchPage.hasNext());

        log.info("📊 Simulation migration {} → {} : {}",
                ancienne.getNom(), nouvelle.getNom(), resultat);

        return resultat;
    }

    @Override
    @Transactional(readOnly = true)
    public MigrationResultat simulerPourInstitut(Long institutId, Long nouvelleAnneeId) {
        Annee_academique ancienne = anneeService.getAnneeActivePourInstitut(institutId);
        Annee_academique nouvelle = anneeService.findEntityById(nouvelleAnneeId);
        MigrationResultat resultat = new MigrationResultat();
        int page = 0;
        Page<Inscription> batchPage;
        do {
            Pageable pageable = PageRequest.of(page++, PAGINATION_BATCH_SIZE);
            batchPage = inscriptionRepo.findByAnneeAcademiqueIdPaginated(ancienne.getId(), pageable);
            batchPage.forEach(i -> {
                DecisionFinAnnee decision = i.getDecisionFinAnnee();
                if (decision == null) { resultat.ajouterIgnore(i.getEtudiant().getMatricule()); return; }
                switch (decision) {
                    case ADMIS -> {
                        if (niveauService.getNiveauSuperieur(i.getClasse().getNiveau()).isEmpty())
                            resultat.ajouterDiplome(i.getEtudiant().getMatricule());
                        else resultat.ajouterAdmis(i.getEtudiant().getMatricule());
                    }
                    case REDOUBLANT -> resultat.ajouterRedoublant(i.getEtudiant().getMatricule());
                    case EXCLU     -> resultat.ajouterExclu(i.getEtudiant().getMatricule());
                    case DIPLOME   -> resultat.ajouterDiplome(i.getEtudiant().getMatricule());
                }
            });
        } while (batchPage.hasNext());
        return resultat;
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getEtudiantsSansDecision() {
        Long institutId = securityService.getInstitutIdCourantObligatoire();
        Annee_academique anneeActive = anneeService.getAnneeActivePourInstitut(institutId);
        return inscriptionRepo.findByAnneeAcademiqueIdAndInstitutId(anneeActive.getId(), institutId).stream()
                .filter(i -> i.getDecisionFinAnnee() == null)
                .map(i -> i.getEtudiant().getMatricule()
                        + " - " + i.getEtudiant().getNom()
                        + " " + i.getEtudiant().getPrenom())
                .toList();
    }

    @Override
    public MigrationResultat migrerEtudiant(Long etudiantId, Long nouvelleAnneeId, Utilisateur acteur) {
        return null;
    }

    @Override
    public MigrationResultat migrerEnseignant(Long enseignantId, Long nouvelleAnneeId, Utilisateur acteur) {
        return null;
    }



    // ═══════════════════════════════════════════════════════════
    // MÉTHODES PRIVÉES — LOGIQUE MÉTIER
    // ═══════════════════════════════════════════════════════════

    /**
     * Traitement d'une inscription individuelle selon la décision de fin d'année.
     * Aucune modification des données de N dans tous les cas.
     */
    private void traiterInscriptionPourMigration(
            Inscription ancienne,
            Annee_academique nouvelleAnnee,
            MigrationBatch batch,
            MigrationResultat resultat) {

        DecisionFinAnnee decision = ancienne.getDecisionFinAnnee();

        if (decision == null) {
            decisionRepo.save(decisionBuilder(batch, MigrationSourceType.INSCRIPTION,
                    ancienne.getId(), ancienne.getEtudiant().getMatricule(), MigrationDecisionStatus.ERREUR));
            resultat.ajouterIgnore(ancienne.getEtudiant().getMatricule());
            return;
        }

        switch (decision) {
            case ADMIS -> {
                Optional<Niveau> niveauSup = niveauService.getNiveauSuperieur(
                        ancienne.getClasse().getNiveau());
                if (niveauSup.isEmpty()) {
                    resultat.ajouterDiplome(ancienne.getEtudiant().getMatricule());
                } else {
                    Classe classeSuperieure = trouverClasseCorrespondante(
                            ancienne.getClasse(), niveauSup.get(), nouvelleAnnee);
                    creerNouvelleInscription(ancienne.getEtudiant(), classeSuperieure, nouvelleAnnee);
                    mettreAJourClasseEtudiant(ancienne.getEtudiant(), classeSuperieure);
                    resultat.ajouterAdmis(ancienne.getEtudiant().getMatricule());
                }
            }
            case REDOUBLANT -> {
                creerNouvelleInscription(ancienne.getEtudiant(), ancienne.getClasse(), nouvelleAnnee);
                mettreAJourClasseEtudiant(ancienne.getEtudiant(), ancienne.getClasse());
                resultat.ajouterRedoublant(ancienne.getEtudiant().getMatricule());
            }
            case EXCLU -> {
                creerInscriptionExclusion(ancienne.getEtudiant(), ancienne.getClasse(), nouvelleAnnee);
                ancienne.getEtudiant().setActive(false);
                etudiantRepository.save(ancienne.getEtudiant());
                resultat.ajouterExclu(ancienne.getEtudiant().getMatricule());
            }
            case DIPLOME -> resultat.ajouterDiplome(ancienne.getEtudiant().getMatricule());
        }

        decisionRepo.save(decisionBuilder(batch, MigrationSourceType.INSCRIPTION,
                ancienne.getId(), ancienne.getEtudiant().getMatricule(), MigrationDecisionStatus.MIGREE));
    }

    /**
     * Migre un assistant pédagogique.
     * Réaffecte automatiquement ses mêmes classes pour N+1.
     * Les classes sont des entités permanentes (pas liées à une année),
     * donc la réaffectation est directe.
     */
    private void migrerAssistant(
            AssistantPedagogique assistant,
            Annee_academique nouvelleAnnee,
            MigrationBatch batch,
            MigrationResultat resultat) {

        // Les classes de l'assistant sont déjà les bonnes — elles existent
        // en N+1 puisque les classes sont des entités permanentes non annualisées.
        // Aucune modification nécessaire, on trace juste la décision.
        Collection<Classe> classes = assistant.getClasses();

        if (classes == null || classes.isEmpty()) {
            log.warn("⚠️ Assistant {} n'a aucune classe affectée", assistant.getEmail());
        } else {
            log.info("✅ Assistant {} conserve {} classe(s) pour N+1",
                    assistant.getEmail(), classes.size());
        }

        decisionRepo.save(decisionBuilder(batch, MigrationSourceType.ASSISTANT,
                assistant.getId(), assistant.getEmail(), MigrationDecisionStatus.MIGREE));
        resultat.ajouterAssistant(assistant.getEmail());
    }

    /**
     * Migre tous les assistants pédagogiques d'un institut.
     * Appelé lors de la migration complète.
     */
    private void migrerTousLesAssistants(
            Long institutId,
            Annee_academique nouvelleAnnee,
            MigrationBatch batch,
            MigrationResultat resultat) {

        List<AssistantPedagogique> assistants =
                assistantRepository.findByInstitutId(institutId);

        for (AssistantPedagogique assistant : assistants) {
            migrerAssistant(assistant, nouvelleAnnee, batch, resultat);
        }
    }

    private void migrerInscriptionsDesClasses(
            List<Classe> classes,
            MigrationContext ctx,
            MigrationBatch batch,
            MigrationResultat resultat) {

        for (Classe classe : classes) {
            int page = 0;
            Page<Inscription> batchPage;
            do {
                Pageable pageable = PageRequest.of(page++, PAGINATION_BATCH_SIZE);
                batchPage = inscriptionRepo.findByClasseIdAndAnneeAcademiqueIdPaginated(
                        classe.getId(), ctx.ancienne().getId(), pageable);
                batchPage.forEach(i ->
                        traiterInscriptionPourMigration(i, ctx.nouvelle(), batch, resultat));
            } while (batchPage.hasNext());
        }
    }

    private void creerNouvelleInscription(Etudiant etudiant, Classe classe, Annee_academique annee) {
        if (inscriptionRepo.existsByEtudiantIdAndAnneeAcademiqueId(etudiant.getId(), annee.getId())) return;
        inscriptionRepo.save(Inscription.builder()
                .etudiant(etudiant).classe(classe).anneeAcademique(annee)
                .statut(StatutInscription.ACTIF).build());
    }

    private void creerInscriptionExclusion(Etudiant etudiant, Classe classe, Annee_academique annee) {
        if (inscriptionRepo.existsByEtudiantIdAndAnneeAcademiqueId(etudiant.getId(), annee.getId())) return;
        inscriptionRepo.save(Inscription.builder()
                .etudiant(etudiant).classe(classe).anneeAcademique(annee)
                .statut(StatutInscription.EXCLU).build());
    }

    private void mettreAJourClasseEtudiant(Etudiant etudiant, Classe nouvelleClasse) {
        etudiant.setClasse(nouvelleClasse);
        etudiant.setFiliere(nouvelleClasse.getFiliere());
        etudiantRepository.save(etudiant);
    }

    private void restaurerClasseEtudiant(Etudiant etudiant, Annee_academique anneeSource) {
        inscriptionRepo.findByEtudiantIdAndAnneeAcademiqueId(etudiant.getId(), anneeSource.getId())
                .ifPresentOrElse(i -> {
                    etudiant.setClasse(i.getClasse());
                    etudiant.setFiliere(i.getClasse().getFiliere());
                    etudiantRepository.save(etudiant);
                }, () -> log.warn("⚠️ Inscription N introuvable pour rollback étudiant : {}",
                        etudiant.getMatricule()));
    }

    private Classe trouverClasseCorrespondante(
            Classe actuelle, Niveau niveauSup, Annee_academique nouvelleAnnee) {

        Long specialiteId = Optional.ofNullable(actuelle.getNiveau())
                .map(Niveau::getSpecialite).map(Specialite::getId).orElse(null);

        List<Classe> candidates = classesRepo.findByNiveauId(niveauSup.getId());

        if (specialiteId != null) {
            candidates = candidates.stream()
                    .filter(c -> Optional.ofNullable(c.getNiveau())
                            .map(Niveau::getSpecialite).map(Specialite::getId)
                            .map(id -> id.equals(specialiteId)).orElse(false))
                    .toList();
        }

        return candidates.stream()
                .filter(c -> !c.isPleine(nouvelleAnnee))
                .min((c1, c2) -> Integer.compare(
                        c1.getNombreEtudiants(nouvelleAnnee),
                        c2.getNombreEtudiants(nouvelleAnnee)))
                .orElseThrow(() -> new MigrationException(
                        "Toutes les classes du niveau supérieur sont pleines pour "
                                + nouvelleAnnee.getNom() + " (niveau : " + niveauSup.getNom() + ")"));
    }

    private void terminerBatch(MigrationBatch batch) {
        batch.setStatus(MigrationBatchStatus.TERMINEE);
        batch.setRollbackPossible(true);
        batchRepo.save(batch);
    }

    private void publierContexteActif(Long institutId, Annee_academique nouvelle) {
        Semestre s1 = semestreService.getByAnnee(nouvelle.getId()).stream()
                .filter(s -> s.getTypeSemestre().name().contains("1"))
                .findFirst()
                .orElseThrow(() -> new MigrationException("Semestre 1 introuvable pour la nouvelle année"));

        contexteRepo.findByInstitutId(institutId).ifPresentOrElse(ctx -> {
            ctx.setAnneeAcademique(nouvelle);
            ctx.setSemestre(s1);
            ctx.setDerniereBascule(LocalDateTime.now());
            contexteRepo.save(ctx);
        }, () -> contexteRepo.save(InstitutContexteActif.builder()
                .institut(Institut.builder().id(institutId).build())
                .anneeAcademique(nouvelle).semestre(s1)
                .derniereBascule(LocalDateTime.now()).build()));
    }

    private void restaurerContexteActif(Long institutId, Annee_academique anneeSource) {
        Semestre s1 = semestreService.getByAnnee(anneeSource.getId()).stream()
                .filter(s -> s.getTypeSemestre().name().contains("1"))
                .findFirst()
                .orElseThrow(() -> new MigrationException(
                        "Semestre 1 introuvable pour l'année source : " + anneeSource.getNom()));

        contexteRepo.findByInstitutId(institutId).ifPresent(ctx -> {
            ctx.setAnneeAcademique(anneeSource);
            ctx.setSemestre(s1);
            ctx.setDerniereBascule(LocalDateTime.now());
            contexteRepo.save(ctx);
        });
    }

    private MigrationContext prepareMigrationContext(
            Long institutId, Long nouvelleAnneeId, Utilisateur acteur) {
        securityService.checkManageInstitut(acteur, institutId);
        Annee_academique ancienne = anneeService.getAnneeActivePourInstitut(institutId);
        Annee_academique nouvelle = anneeService.findEntityById(nouvelleAnneeId);
        if (nouvelle == null || nouvelle.isActive())
            throw new MigrationException("L'année cible n'existe pas ou est déjà active");
        if (!nouvelle.getInstitut().getId().equals(institutId))
            throw new MigrationException("L'année cible n'appartient pas au même institut");
        return new MigrationContext(ancienne, nouvelle, institutId);
    }

    private MigrationBatch initierBatch(MigrationContext ctx, Utilisateur acteur) {
        return batchRepo.save(MigrationBatch.builder()
                .institut(Institut.builder().id(ctx.institutId()).build())
                .sourceAnnee(ctx.ancienne()).targetAnnee(ctx.nouvelle())
                .status(MigrationBatchStatus.EN_EXECUTION)
                .dateExecution(LocalDateTime.now())
                .motif("Migration initiée par " + acteur.getEmail())
                .build());
    }

    private MigrationDecision decisionBuilder(
            MigrationBatch batch, MigrationSourceType type,
            Long sourceId, String ref, MigrationDecisionStatus status) {
        return MigrationDecision.builder()
                .batch(batch).sourceType(type)
                .sourceId(sourceId).sourceReference(ref)
                .status(status).build();
    }

    private void validerListe(List<Long> ids, String entite) {
        if (ids == null || ids.isEmpty())
            throw new MigrationException("La liste de " + entite + " ne peut pas être vide");
    }

    private String buildDescription(
            Annee_academique ancienne, Annee_academique nouvelle, MigrationResultat resultat) {
        return String.format(
                "Migration %s → %s | Admis: %d, Redoublants: %d, Exclus: %d, Diplômés: %d, Ignorés: %d",
                ancienne.getNom(), nouvelle.getNom(),
                resultat.getAdmis(), resultat.getRedoublants(),
                resultat.getExclus(), resultat.getDiplomes(), resultat.getIgnores());
    }

    private Long getInstitutIdFromClasse(Classe classe) {
        return Optional.ofNullable(classe.getNiveau())
                .map(Niveau::getFiliere).map(Filiere::getInstitutId)
                .orElseThrow(() -> new MigrationException(
                        "Impossible de déterminer l'institut de la classe"));
    }

    private Long getInstitutIdFromUE(UE ue) {
        return Optional.ofNullable(ue.getSpecialite())
                .map(Specialite::getInstitutId)
                .orElseThrow(() -> new MigrationException(
                        "Impossible de déterminer l'institut de l'UE"));
    }

    private record MigrationContext(
            Annee_academique ancienne, Annee_academique nouvelle, Long institutId) {}

    public static class MigrationException extends RuntimeException {
        public MigrationException(String message) { super(message); }
        public MigrationException(String message, Throwable cause) { super(message, cause); }
    }
}