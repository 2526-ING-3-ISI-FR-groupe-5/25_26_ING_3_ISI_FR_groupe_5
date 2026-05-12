package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Enum.DecisionFinAnnee;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Repository.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.InterfaceService.IMigrationService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Services.ServiceImple.ProgrammationUEService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Etudiant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Inscription;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.EnseignantRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.EtudiantRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.InscriptionRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService.IJournalActionService;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MigrationService implements IMigrationService {

    // ─────────────────────────────────────────────────────────────
    // CONFIGURATION PAGINATION
    // ─────────────────────────────────────────────────────────────
    private static final int PAGINATION_BATCH_SIZE = 500;

    // ─────────────────────────────────────────────────────────────
    // DÉPENDANCES
    // ─────────────────────────────────────────────────────────────
    private final InscriptionRepository inscriptionRepo;
    private final EtudiantRepository etudiantRepository;
    private final EnseignantRepository enseignantRepository;
    private final AnneeAcademiqueService anneeService;
    private final NiveauService niveauService;
    private final ClassesRepository classesRepo;
    private final UERepository ueRepository;
    private final FiliereRepository filiereRepository;
    private final ProgrammationUEService programmationService;
    private final IJournalActionService journalService;
    private final InstitutSecurityService securityService;

    // ═══════════════════════════════════════════════════════════
    // MIGRATION COMPLÈTE
    // ═══════════════════════════════════════════════════════════

    @Override
    @Transactional
    public MigrationResultat migrer(Long nouvelleAnneeId, Utilisateur acteur) {
        return migrerPourInstitut(null, nouvelleAnneeId, acteur);
    }

    @Override
    @Transactional
    public MigrationResultat migrerPourInstitut(Long institutId, Long nouvelleAnneeId, Utilisateur acteur) {
        MigrationContext ctx = prepareMigrationContext(institutId, nouvelleAnneeId, acteur);

        log.info("🚀 Migration complète pour l'institut {} : {} → {}",
                ctx.institutId(), ctx.ancienne().getNom(), ctx.nouvelle().getNom());

        MigrationResultat resultat = new MigrationResultat();

        // Migration des inscriptions avec pagination pour éviter OOM
        int page = 0;
        Page<Inscription> batch;
        do {
            Pageable pageable = PageRequest.of(page++, PAGINATION_BATCH_SIZE);
            batch = inscriptionRepo.findByAnneeAcademiqueIdPaginated(ctx.ancienne().getId(), pageable);
            batch.forEach(inscription -> migrerUneInscription(inscription, ctx.nouvelle(), resultat));
        } while (batch.hasNext());

        // Duplication des programmations UE
        programmationService.dupliquerVersNouvelleAnnee(ctx.ancienne().getId(), ctx.nouvelle().getId());

        // Activation de la nouvelle année
        anneeService.activer(ctx.nouvelle().getId(), acteur);

        // Journalisation
        journalService.journaliserMigration(acteur, ctx.nouvelle().getId(),
                buildDescriptionMigration(ctx.ancienne(), ctx.nouvelle(), resultat));

        log.info("✅ Migration complète terminée : {}", resultat);
        return resultat;
    }

    @Override
    public MigrationResultat simuler(Long nouvelleAnneeId) {
        return null;
    }

    // ═══════════════════════════════════════════════════════════
    // SIMULATION (DRY-RUN)
    // ═══════════════════════════════════════════════════════════

    @Transactional(readOnly = true)
    @Override
    public MigrationResultat simuler(Long institutId, Long nouvelleAnneeId) {
        Utilisateur acteur = securityService.getCurrentUserOrThrow();
        Long institutCible = securityService.resolveInstitutId(acteur, institutId);
        if (institutCible == null) {
            throw new AccessDeniedException("Veuillez sélectionner un institut");
        }
        return simulerPourInstitut(institutCible, nouvelleAnneeId);
    }

    @Override
    @Transactional(readOnly = true)
    public MigrationResultat simulerPourInstitut(Long institutId, Long nouvelleAnneeId) {
        Annee_academique ancienne = anneeService.getAnneeActivePourInstitut(institutId);
        Annee_academique nouvelle = anneeService.findEntityById(nouvelleAnneeId);

        log.info("🔮 Simulation de migration pour l'institut {} : {} → {}",
                institutId, ancienne.getNom(), nouvelle.getNom());

        MigrationResultat resultat = new MigrationResultat();

        // Simulation avec pagination
        int page = 0;
        Page<Inscription> batch;
        do {
            Pageable pageable = PageRequest.of(page++, PAGINATION_BATCH_SIZE);
            batch = inscriptionRepo.findByAnneeAcademiqueIdPaginated(ancienne.getId(), pageable);
            batch.forEach(inscription -> {
                DecisionFinAnnee decision = inscription.getDecisionFinAnnee();
                if (decision == null) {
                    resultat.ajouterIgnore(inscription.getEtudiant().getMatricule());
                    return;
                }
                switch (decision) {
                    case ADMIS -> {
                        Niveau niveauActuel = inscription.getClasse().getNiveau();
                        if (niveauService.getNiveauSuperieur(niveauActuel).isEmpty()) {
                            resultat.ajouterDiplome(inscription.getEtudiant().getMatricule());
                        } else {
                            resultat.ajouterAdmis(inscription.getEtudiant().getMatricule());
                        }
                    }
                    case REDOUBLANT -> resultat.ajouterRedoublant(inscription.getEtudiant().getMatricule());
                    case EXCLU -> resultat.ajouterExclu(inscription.getEtudiant().getMatricule());
                    case DIPLOME -> resultat.ajouterDiplome(inscription.getEtudiant().getMatricule());
                }
            });
        } while (batch.hasNext());

        log.info("✅ Simulation terminée : {}", resultat);
        return resultat;
    }

    // ═══════════════════════════════════════════════════════════
    // VÉRIFICATION DES DÉCISIONS MANQUANTES
    // ═══════════════════════════════════════════════════════════

    @Override
    public List<String> getEtudiantsSansDecision() {
        Long institutId = securityService.getInstitutIdCourantObligatoire();
        Annee_academique anneeActive = anneeService.getAnneeActivePourInstitut(institutId);

        // On récupère toutes les inscriptions via pagination
        List<String> result = new java.util.ArrayList<>();
        int page = 0;
        Page<Inscription> batch;
        do {
            Pageable pageable = PageRequest.of(page++, PAGINATION_BATCH_SIZE);
            batch = inscriptionRepo.findByAnneeAcademiqueIdPaginated(anneeActive.getId(), pageable);
            batch.stream()
                    .filter(i -> i.getDecisionFinAnnee() == null)
                    .map(i -> i.getEtudiant().getMatricule() + " - " +
                            i.getEtudiant().getNom() + " " + i.getEtudiant().getPrenom())
                    .forEach(result::add);
        } while (batch.hasNext());

        return result;
    }

    // ═══════════════════════════════════════════════════════════
    // MIGRATIONS SÉLECTIVES
    // ═══════════════════════════════════════════════════════════

    @Override
    @Transactional
    public MigrationResultat migrerEtudiant(Long etudiantId, Long nouvelleAnneeId, Utilisateur acteur) {
        Etudiant etudiant = etudiantRepository.findById(etudiantId)
                .orElseThrow(() -> new MigrationException("Étudiant introuvable : " + etudiantId));

        MigrationContext ctx = prepareMigrationContext(etudiant.getInstitut().getId(), nouvelleAnneeId, acteur);

        Inscription inscription = inscriptionRepo
                .findByEtudiantIdAndAnneeAcademiqueId(etudiantId, ctx.ancienne().getId())
                .orElseThrow(() -> new MigrationException("Inscription introuvable pour l'étudiant : " + etudiantId));

        MigrationResultat resultat = new MigrationResultat();
        migrerUneInscription(inscription, ctx.nouvelle(), resultat);

        journalService.journaliserMigration(acteur, ctx.nouvelle().getId(),
                "Migration sélective étudiant : " + etudiant.getNom() + " " + etudiant.getPrenom() +
                        " (" + etudiant.getMatricule() + ") → " + ctx.nouvelle().getNom());

        log.info("✅ Migration étudiant {} terminée", etudiant.getMatricule());
        return resultat;
    }

    @Override
    @Transactional
    public MigrationResultat migrerEnseignant(Long enseignantId, Long nouvelleAnneeId, Utilisateur acteur) {
        Enseignant enseignant = enseignantRepository.findById(enseignantId)
                .orElseThrow(() -> new MigrationException("Enseignant introuvable : " + enseignantId));

        MigrationContext ctx = prepareMigrationContext(enseignant.getInstitut().getId(), nouvelleAnneeId, acteur);

        MigrationResultat resultat = new MigrationResultat();
        programmationService.dupliquerEnseignantVersNouvelleAnnee(enseignantId, ctx.ancienne().getId(), ctx.nouvelle().getId());
        resultat.ajouterAdmis("Programmations de " + enseignant.getNom() + " migrées");

        journalService.journaliserMigration(acteur, ctx.nouvelle().getId(),
                "Migration sélective enseignant : " + enseignant.getNom() + " → " + ctx.nouvelle().getNom());

        log.info("✅ Migration enseignant {} terminée", enseignant.getNom());
        return resultat;
    }

    @Override
    @Transactional
    public MigrationResultat migrerUE(Long ueId, Long nouvelleAnneeId, Utilisateur acteur) {
        UE ue = ueRepository.findById(ueId)
                .orElseThrow(() -> new MigrationException("UE introuvable : " + ueId));

        Long institutId = getInstitutIdFromUE(ue);
        MigrationContext ctx = prepareMigrationContext(institutId, nouvelleAnneeId, acteur);

        MigrationResultat resultat = new MigrationResultat();
        programmationService.dupliquerUEVersNouvelleAnnee(ueId, ctx.ancienne().getId(), ctx.nouvelle().getId());
        resultat.ajouterAdmis("UE " + ue.getNom() + " migrée");

        journalService.journaliserMigration(acteur, ctx.nouvelle().getId(),
                "Migration sélective UE : " + ue.getNom() + " (" + ue.getCode() + ") → " + ctx.nouvelle().getNom());

        log.info("✅ Migration UE {} terminée", ue.getNom());
        return resultat;
    }

    @Override
    @Transactional
    public MigrationResultat migrerClasse(Long classeId, Long nouvelleAnneeId, Utilisateur acteur) {
        Classe classe = classesRepo.findById(classeId)
                .orElseThrow(() -> new MigrationException("Classe introuvable : " + classeId));

        Long institutId = getInstitutIdFromClasse(classe);
        MigrationContext ctx = prepareMigrationContext(institutId, nouvelleAnneeId, acteur);

        MigrationResultat resultat = new MigrationResultat();

        // Pagination sur les inscriptions d'une classe
        int page = 0;
        Page<Inscription> batch;
        do {
            Pageable pageable = PageRequest.of(page++, PAGINATION_BATCH_SIZE);
            batch = inscriptionRepo.findByClasseIdAndAnneeAcademiqueIdPaginated(classeId, ctx.ancienne().getId(), pageable);
            batch.forEach(inscription -> migrerUneInscription(inscription, ctx.nouvelle(), resultat));
        } while (batch.hasNext());

        journalService.journaliserMigration(acteur, ctx.nouvelle().getId(),
                "Migration sélective classe : " + classe.getNom() + " → " + ctx.nouvelle().getNom());

        log.info("✅ Migration classe {} terminée", classe.getNom());
        return resultat;
    }

    @Override
    @Transactional
    public MigrationResultat migrerFiliere(Long filiereId, Long nouvelleAnneeId, Utilisateur acteur) {
        Filiere filiere = filiereRepository.findById(filiereId)
                .orElseThrow(() -> new MigrationException("Filière introuvable : " + filiereId));

        Long institutId = filiere.getInstitutId(); // ✅ Utilisation du helper de ton entity
        MigrationContext ctx = prepareMigrationContext(institutId, nouvelleAnneeId, acteur);

        MigrationResultat resultat = new MigrationResultat();

        List<Niveau> niveaux = niveauService.getByFiliere(filiereId);
        for (Niveau niveau : niveaux) {
            List<Classe> classes = classesRepo.findByNiveauId(niveau.getId());
            for (Classe classe : classes) {
                int page = 0;
                Page<Inscription> batch;
                do {
                    Pageable pageable = PageRequest.of(page++, PAGINATION_BATCH_SIZE);
                    batch = inscriptionRepo.findByClasseIdAndAnneeAcademiqueIdPaginated(classe.getId(), ctx.ancienne().getId(), pageable);
                    batch.forEach(inscription -> migrerUneInscription(inscription, ctx.nouvelle(), resultat));
                } while (batch.hasNext());
            }
        }

        journalService.journaliserMigration(acteur, ctx.nouvelle().getId(),
                "Migration sélective filière : " + filiere.getNom() + " → " + ctx.nouvelle().getNom());

        log.info("✅ Migration filière {} terminée", filiere.getNom());
        return resultat;
    }

    @Override
    @Transactional
    public MigrationResultat migrerNiveau(Long niveauId, Long nouvelleAnneeId, Utilisateur acteur) {
        Niveau niveau = niveauService.findById(niveauId);

        Long institutId = niveau.getFiliere() != null
                ? niveau.getFiliere().getInstitutId() // ✅ Helper de l'entity
                : null;
        if (institutId == null) {
            throw new MigrationException("Impossible de déterminer l'institut du niveau");
        }

        MigrationContext ctx = prepareMigrationContext(institutId, nouvelleAnneeId, acteur);

        MigrationResultat resultat = new MigrationResultat();

        List<Classe> classes = classesRepo.findByNiveauId(niveauId);
        for (Classe classe : classes) {
            int page = 0;
            Page<Inscription> batch;
            do {
                Pageable pageable = PageRequest.of(page++, PAGINATION_BATCH_SIZE);
                batch = inscriptionRepo.findByClasseIdAndAnneeAcademiqueIdPaginated(classe.getId(), ctx.ancienne().getId(), pageable);
                batch.forEach(inscription -> migrerUneInscription(inscription, ctx.nouvelle(), resultat));
            } while (batch.hasNext());
        }

        journalService.journaliserMigration(acteur, ctx.nouvelle().getId(),
                "Migration sélective niveau : " + niveau.getNom() + " → " + ctx.nouvelle().getNom());

        log.info("✅ Migration niveau {} terminée", niveau.getNom());
        return resultat;
    }

    // ═══════════════════════════════════════════════════════════
    // MÉTHODES PRIVÉES - LOGIQUE MÉTIER
    // ═══════════════════════════════════════════════════════════

    /**
     * Prépare le contexte de migration avec toutes les vérifications de sécurité
     */
    private MigrationContext prepareMigrationContext(Long institutId, Long nouvelleAnneeId, Utilisateur acteur) {
        securityService.checkManageInstitut(acteur, institutId);

        Annee_academique ancienne = anneeService.getAnneeActivePourInstitut(institutId);
        Annee_academique nouvelle = anneeService.findEntityById(nouvelleAnneeId);

        if (nouvelle.isActive()) {
            throw new MigrationException("L'année académique '" + nouvelle.getNom() + "' est déjà active");
        }
        if (!nouvelle.getInstitut().getId().equals(institutId)) {
            throw new MigrationException("L'année cible n'appartient pas au même institut");
        }

        return new MigrationContext(ancienne, nouvelle, institutId);
    }

    /**
     * Migre une inscription individuelle selon sa décision de fin d'année
     */
    private void migrerUneInscription(Inscription ancienne, Annee_academique nouvelleAnnee, MigrationResultat resultat) {
        DecisionFinAnnee decision = ancienne.getDecisionFinAnnee();

        if (decision == null) {
            resultat.ajouterIgnore(ancienne.getEtudiant().getMatricule());
            return;
        }

        switch (decision) {
            case ADMIS -> migrerAdmis(ancienne, nouvelleAnnee, resultat);
            case REDOUBLANT -> migrerRedoublant(ancienne, nouvelleAnnee, resultat);
            case EXCLU -> migrerExclu(ancienne, resultat);
            case DIPLOME -> resultat.ajouterDiplome(ancienne.getEtudiant().getMatricule());
        }
    }

    private void migrerAdmis(Inscription ancienne, Annee_academique nouvelleAnnee, MigrationResultat resultat) {
        Niveau niveauActuel = ancienne.getClasse().getNiveau();
        Optional<Niveau> niveauSup = niveauService.getNiveauSuperieur(niveauActuel);

        if (niveauSup.isEmpty()) {
            resultat.ajouterDiplome(ancienne.getEtudiant().getMatricule());
            return;
        }

        Classe classeSuperieure = trouverClasseCorrespondante(ancienne.getClasse(), niveauSup.get());
        creerNouvelleInscription(ancienne.getEtudiant(), classeSuperieure, nouvelleAnnee);
        resultat.ajouterAdmis(ancienne.getEtudiant().getMatricule());
    }

    private void migrerRedoublant(Inscription ancienne, Annee_academique nouvelleAnnee, MigrationResultat resultat) {
        creerNouvelleInscription(ancienne.getEtudiant(), ancienne.getClasse(), nouvelleAnnee);
        resultat.ajouterRedoublant(ancienne.getEtudiant().getMatricule());
    }

    private void migrerExclu(Inscription ancienne, MigrationResultat resultat) {
        Etudiant etudiant = ancienne.getEtudiant();
        etudiant.setActive(false);
        etudiantRepository.save(etudiant); // ✅ Persistance de la modification
        resultat.ajouterExclu(etudiant.getMatricule());
    }

    /**
     * Trouve la classe correspondante dans le niveau supérieur
     */
    private Classe trouverClasseCorrespondante(Classe actuelle, Niveau niveauSup) {
        List<Classe> candidates = classesRepo.findByNiveauId(niveauSup.getId());

        Long specialiteId = Optional.ofNullable(actuelle.getNiveau())
                .map(Niveau::getSpecialite)
                .map(Specialite::getId)
                .orElse(null);

        if (specialiteId != null) {
            candidates = candidates.stream()
                    .filter(c -> Optional.ofNullable(c.getNiveau())
                            .map(Niveau::getSpecialite)
                            .map(Specialite::getId)
                            .map(id -> id.equals(specialiteId))
                            .orElse(false))
                    .toList();
        }

        return candidates.stream().findFirst()
                .orElseThrow(() -> new MigrationException(
                        "Aucune classe trouvée pour le niveau supérieur : " + niveauSup.getNom()));
    }

    /**
     * Crée une nouvelle inscription si elle n'existe pas déjà
     */
    private void creerNouvelleInscription(Etudiant etudiant, Classe classe, Annee_academique annee) {
        if (inscriptionRepo.existsByEtudiantIdAndAnneeAcademiqueId(etudiant.getId(), annee.getId())) {
            return;
        }
        Inscription nouvelle = new Inscription();
        nouvelle.setEtudiant(etudiant);
        nouvelle.setClasse(classe);
        nouvelle.setAnneeAcademique(annee);
        inscriptionRepo.save(nouvelle);
    }

    private String buildDescriptionMigration(Annee_academique ancienne, Annee_academique nouvelle, MigrationResultat resultat) {
        return String.format(
                "Migration %s → %s | Admis: %d, Redoublants: %d, Exclus: %d, Diplômés: %d, Ignorés: %d",
                ancienne.getNom(), nouvelle.getNom(),
                resultat.getAdmis(), resultat.getRedoublants(),
                resultat.getExclus(), resultat.getDiplomes(),
                resultat.getIgnores()
        );
    }

    // ═══════════════════════════════════════════════════════════
    // MÉTHODES UTILITAIRES - RÉSOLUTION D'INSTITUT (simplifiées avec tes helpers)
    // ═══════════════════════════════════════════════════════════

    private Long getInstitutIdFromClasse(Classe classe) {
        return Optional.ofNullable(classe.getNiveau())
                .map(Niveau::getFiliere)
                .map(Filiere::getInstitutId) // ✅ Helper de ton entity Filiere
                .orElseThrow(() -> new MigrationException("Impossible de déterminer l'institut de la classe"));
    }

    private Long getInstitutIdFromUE(UE ue) {
        return Optional.ofNullable(ue.getSpecialite())
                .map(Specialite::getInstitutId) // ✅ Helper de ton entity Specialite
                .orElseThrow(() -> new MigrationException("Impossible de déterminer l'institut de l'UE"));
    }

    // ═══════════════════════════════════════════════════════════
    // RECORDS & EXCEPTIONS MÉTIER
    // ═══════════════════════════════════════════════════════════

    /**
     * Contexte de migration immuable pour éviter les erreurs de paramètres
     */
    private record MigrationContext(
            Annee_academique ancienne,
            Annee_academique nouvelle,
            Long institutId
    ) {}

    /**
     * Exception métier pour les erreurs de migration
     */
    public static class MigrationException extends RuntimeException {
        public MigrationException(String message) {
            super(message);
        }
        public MigrationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}