package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Inscription;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.DecisionFinAnnee;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService.IJournalActionService;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MigrationService {

    private final InscriptionRepository inscriptionRepo;
    private final AnneeAcademiqueService anneeService;
    private final NiveauService niveauService;
    private final ClassesRepository classesRepo;
    private final ProgrammationUEService programmationService;
    private final EtudiantRepository etudiantRepository;
    private final EnseignantRepository enseignantRepository;
    private final UERepository ueRepository;
    private final FiliereRepository filiereRepository;
    private final IJournalActionService journalService;

    // ═══════════════════════════════════════════════════════════
    // MIGRATION COMPLÈTE
    // ═══════════════════════════════════════════════════════════

    @Transactional
    public MigrationResultat migrer(Long nouvelleAnneeId, Utilisateur acteur) {
        Annee_academique ancienneAnnee = anneeService.getAnneeActive();
        Annee_academique nouvelleAnnee = anneeService.findEntityById(nouvelleAnneeId);

        if (nouvelleAnnee.isActive()) {
            throw new RuntimeException("Cette année est déjà active");
        }

        log.info("🚀 Migration complète : {} → {}", ancienneAnnee.getNom(), nouvelleAnnee.getNom());

        MigrationResultat resultat = new MigrationResultat();

        List<Inscription> anciennes = inscriptionRepo.findByAnneeAcademiqueId(ancienneAnnee.getId());
        migrerInscriptions(anciennes, nouvelleAnnee, resultat);

        programmationService.dupliquerVersNouvelleAnnee(ancienneAnnee.getId(), nouvelleAnnee.getId());

        anneeService.activer(nouvelleAnneeId, acteur);

        journalService.journaliserMigration(acteur, nouvelleAnneeId,
                buildDescriptionMigration(ancienneAnnee, nouvelleAnnee, resultat));

        log.info("✅ Migration complète terminée : {}", resultat);
        return resultat;
    }

    // ═══════════════════════════════════════════════════════════
    // SIMULATION (DRY-RUN)
    // ═══════════════════════════════════════════════════════════

    @Transactional(readOnly = true)
    public MigrationResultat simuler(Long nouvelleAnneeId) {
        Annee_academique ancienneAnnee = anneeService.getAnneeActive();
        Annee_academique nouvelleAnnee = anneeService.findEntityById(nouvelleAnneeId);

        log.info("🔮 Simulation de migration : {} → {}", ancienneAnnee.getNom(), nouvelleAnnee.getNom());

        MigrationResultat resultat = new MigrationResultat();
        List<Inscription> anciennes = inscriptionRepo.findByAnneeAcademiqueId(ancienneAnnee.getId());

        for (Inscription inscription : anciennes) {
            DecisionFinAnnee decision = inscription.getDecisionFinAnnee();

            if (decision == null) {
                resultat.ajouterIgnore(inscription.getEtudiant().getMatricule());
                continue;
            }

            switch (decision) {
                case ADMIS -> {
                    Niveau niveauActuel = inscription.getClasse().getNiveau();
                    Optional<Niveau> niveauSup = niveauService.getNiveauSuperieur(niveauActuel);
                    if (niveauSup.isEmpty()) {
                        resultat.ajouterDiplome(inscription.getEtudiant().getMatricule());
                    } else {
                        resultat.ajouterAdmis(inscription.getEtudiant().getMatricule());
                    }
                }
                case REDOUBLANT -> resultat.ajouterRedoublant(inscription.getEtudiant().getMatricule());
                case EXCLU -> resultat.ajouterExclu(inscription.getEtudiant().getMatricule());
                case DIPLOME -> resultat.ajouterDiplome(inscription.getEtudiant().getMatricule());
            }
        }

        log.info("✅ Simulation terminée : {}", resultat);
        return resultat;
    }

    // ═══════════════════════════════════════════════════════════
    // VÉRIFICATION DES DÉCISIONS MANQUANTES
    // ═══════════════════════════════════════════════════════════

    public List<String> getEtudiantsSansDecision() {
        Annee_academique anneeActive = anneeService.getAnneeActive();
        return inscriptionRepo.findByAnneeAcademiqueId(anneeActive.getId())
                .stream()
                .filter(i -> i.getDecisionFinAnnee() == null)
                .map(i -> i.getEtudiant().getMatricule() + " - " +
                        i.getEtudiant().getNom() + " " + i.getEtudiant().getPrenom())
                .toList();
    }

    // ═══════════════════════════════════════════════════════════
    // MIGRATION SÉLECTIVE : ÉTUDIANT
    // ═══════════════════════════════════════════════════════════

    @Transactional
    public MigrationResultat migrerEtudiant(Long etudiantId, Long nouvelleAnneeId, Utilisateur acteur) {
        Etudiant etudiant = etudiantRepository.findById(etudiantId)
                .orElseThrow(() -> new RuntimeException("Étudiant introuvable"));

        Annee_academique ancienneAnnee = anneeService.getAnneeActive();
        Annee_academique nouvelleAnnee = anneeService.findEntityById(nouvelleAnneeId);

        MigrationResultat resultat = new MigrationResultat();

        Inscription inscription = inscriptionRepo
                .findByEtudiantIdAndAnneeAcademiqueId(etudiantId, ancienneAnnee.getId())
                .orElseThrow(() -> new RuntimeException("Inscription introuvable pour cet étudiant"));

        migrerUneInscription(inscription, nouvelleAnnee, resultat);

        journalService.journaliserMigration(acteur, nouvelleAnneeId,
                "Migration sélective étudiant : " + etudiant.getNom() + " " + etudiant.getPrenom() +
                        " (" + etudiant.getMatricule() + ") → " + nouvelleAnnee.getNom());

        log.info("✅ Migration étudiant {} terminée", etudiant.getMatricule());
        return resultat;
    }

    // ═══════════════════════════════════════════════════════════
    // MIGRATION SÉLECTIVE : ENSEIGNANT
    // ═══════════════════════════════════════════════════════════

    @Transactional
    public MigrationResultat migrerEnseignant(Long enseignantId, Long nouvelleAnneeId, Utilisateur acteur) {
        Enseignant enseignant = enseignantRepository.findById(enseignantId)
                .orElseThrow(() -> new RuntimeException("Enseignant introuvable"));

        Annee_academique ancienneAnnee = anneeService.getAnneeActive();
        Annee_academique nouvelleAnnee = anneeService.findEntityById(nouvelleAnneeId);

        MigrationResultat resultat = new MigrationResultat();

        programmationService.dupliquerEnseignantVersNouvelleAnnee(enseignantId, ancienneAnnee.getId(), nouvelleAnnee.getId());

        resultat.ajouterAdmis("Programmations de " + enseignant.getNom() + " migrées");

        journalService.journaliserMigration(acteur, nouvelleAnneeId,
                "Migration sélective enseignant : " + enseignant.getNom() + " " + enseignant.getPrenom() +
                        " → " + nouvelleAnnee.getNom());

        log.info("✅ Migration enseignant {} terminée", enseignant.getNom());
        return resultat;
    }

    // ═══════════════════════════════════════════════════════════
    // MIGRATION SÉLECTIVE : UE
    // ═══════════════════════════════════════════════════════════

    @Transactional
    public MigrationResultat migrerUE(Long ueId, Long nouvelleAnneeId, Utilisateur acteur) {
        UE ue = ueRepository.findById(ueId)
                .orElseThrow(() -> new RuntimeException("UE introuvable"));

        Annee_academique ancienneAnnee = anneeService.getAnneeActive();
        Annee_academique nouvelleAnnee = anneeService.findEntityById(nouvelleAnneeId);

        MigrationResultat resultat = new MigrationResultat();

        programmationService.dupliquerUEVersNouvelleAnnee(ueId, ancienneAnnee.getId(), nouvelleAnnee.getId());

        resultat.ajouterAdmis("UE " + ue.getNom() + " migrée");

        journalService.journaliserMigration(acteur, nouvelleAnneeId,
                "Migration sélective UE : " + ue.getNom() + " (" + ue.getCode() + ") → " + nouvelleAnnee.getNom());

        log.info("✅ Migration UE {} terminée", ue.getNom());
        return resultat;
    }

    // ═══════════════════════════════════════════════════════════
    // MIGRATION SÉLECTIVE : CLASSE
    // ═══════════════════════════════════════════════════════════

    @Transactional
    public MigrationResultat migrerClasse(Long classeId, Long nouvelleAnneeId, Utilisateur acteur) {
        Classe classe = classesRepo.findById(classeId)
                .orElseThrow(() -> new RuntimeException("Classe introuvable"));

        Annee_academique ancienneAnnee = anneeService.getAnneeActive();
        Annee_academique nouvelleAnnee = anneeService.findEntityById(nouvelleAnneeId);

        MigrationResultat resultat = new MigrationResultat();

        List<Inscription> inscriptions = inscriptionRepo.findByClasseIdAndAnneeAcademiqueId(classeId, ancienneAnnee.getId());
        migrerInscriptions(inscriptions, nouvelleAnnee, resultat);

        journalService.journaliserMigration(acteur, nouvelleAnneeId,
                "Migration sélective classe : " + classe.getNom() + " → " + nouvelleAnnee.getNom());

        log.info("✅ Migration classe {} terminée", classe.getNom());
        return resultat;
    }

    // ═══════════════════════════════════════════════════════════
    // MIGRATION SÉLECTIVE : FILIÈRE
    // ═══════════════════════════════════════════════════════════

    @Transactional
    public MigrationResultat migrerFiliere(Long filiereId, Long nouvelleAnneeId, Utilisateur acteur) {
        Filiere filiere = filiereRepository.findById(filiereId)
                .orElseThrow(() -> new RuntimeException("Filière introuvable"));

        Annee_academique ancienneAnnee = anneeService.getAnneeActive();
        Annee_academique nouvelleAnnee = anneeService.findEntityById(nouvelleAnneeId);

        MigrationResultat resultat = new MigrationResultat();

        List<Niveau> niveaux = niveauService.getByFiliere(filiereId);
        for (Niveau niveau : niveaux) {
            List<Classe> classes = classesRepo.findByNiveauId(niveau.getId());
            for (Classe classe : classes) {
                List<Inscription> inscriptions = inscriptionRepo.findByClasseIdAndAnneeAcademiqueId(classe.getId(), ancienneAnnee.getId());
                migrerInscriptions(inscriptions, nouvelleAnnee, resultat);
            }
        }

        journalService.journaliserMigration(acteur, nouvelleAnneeId,
                "Migration sélective filière : " + filiere.getNom() + " → " + nouvelleAnnee.getNom());

        log.info("✅ Migration filière {} terminée", filiere.getNom());
        return resultat;
    }

    // ═══════════════════════════════════════════════════════════
    // MIGRATION SÉLECTIVE : NIVEAU
    // ═══════════════════════════════════════════════════════════

    @Transactional
    public MigrationResultat migrerNiveau(Long niveauId, Long nouvelleAnneeId, Utilisateur acteur) {
        Niveau niveau = niveauService.findById(niveauId);

        Annee_academique ancienneAnnee = anneeService.getAnneeActive();
        Annee_academique nouvelleAnnee = anneeService.findEntityById(nouvelleAnneeId);

        MigrationResultat resultat = new MigrationResultat();

        List<Classe> classes = classesRepo.findByNiveauId(niveauId);
        for (Classe classe : classes) {
            List<Inscription> inscriptions = inscriptionRepo.findByClasseIdAndAnneeAcademiqueId(classe.getId(), ancienneAnnee.getId());
            migrerInscriptions(inscriptions, nouvelleAnnee, resultat);
        }

        journalService.journaliserMigration(acteur, nouvelleAnneeId,
                "Migration sélective niveau : " + niveau.getNom() + " → " + nouvelleAnnee.getNom());

        log.info("✅ Migration niveau {} terminée", niveau.getNom());
        return resultat;
    }

    // ═══════════════════════════════════════════════════════════
    // MÉTHODES PRIVÉES
    // ═══════════════════════════════════════════════════════════

    private void migrerInscriptions(List<Inscription> inscriptions, Annee_academique nouvelleAnnee, MigrationResultat resultat) {
        for (Inscription inscription : inscriptions) {
            migrerUneInscription(inscription, nouvelleAnnee, resultat);
        }
    }

    private void migrerUneInscription(Inscription ancienne, Annee_academique nouvelleAnnee, MigrationResultat resultat) {
        DecisionFinAnnee decision = ancienne.getDecisionFinAnnee();

        if (decision == null) {
            resultat.ajouterIgnore(ancienne.getEtudiant().getMatricule());
            return;
        }

        switch (decision) {
            case ADMIS -> {
                Niveau niveauActuel = ancienne.getClasse().getNiveau();
                Optional<Niveau> niveauSup = niveauService.getNiveauSuperieur(niveauActuel);

                if (niveauSup.isEmpty()) {
                    resultat.ajouterDiplome(ancienne.getEtudiant().getMatricule());
                    break;
                }

                Classe classeSuperieure = trouverClasseCorrespondante(ancienne.getClasse(), niveauSup.get());

                creerNouvelleInscription(ancienne.getEtudiant(), classeSuperieure, nouvelleAnnee);
                resultat.ajouterAdmis(ancienne.getEtudiant().getMatricule());
            }
            case REDOUBLANT -> {
                creerNouvelleInscription(ancienne.getEtudiant(), ancienne.getClasse(), nouvelleAnnee);
                resultat.ajouterRedoublant(ancienne.getEtudiant().getMatricule());
            }
            case EXCLU -> {
                ancienne.getEtudiant().setActive(false);
                resultat.ajouterExclu(ancienne.getEtudiant().getMatricule());
            }
            case DIPLOME -> {
                resultat.ajouterDiplome(ancienne.getEtudiant().getMatricule());
            }
        }
    }

    private Classe trouverClasseCorrespondante(Classe actuelle, Niveau niveauSup) {
        Long specialiteId = actuelle.getNiveau().getSpecialite() != null
                ? actuelle.getNiveau().getSpecialite().getId()
                : null;

        List<Classe> classesNiveauSup = classesRepo.findByNiveauId(niveauSup.getId());

        if (specialiteId != null) {
            classesNiveauSup = classesNiveauSup.stream()
                    .filter(c -> c.getNiveau() != null &&
                            c.getNiveau().getSpecialite() != null &&
                            c.getNiveau().getSpecialite().getId().equals(specialiteId))
                    .toList();
        }

        if (classesNiveauSup.isEmpty()) {
            throw new RuntimeException("Aucune classe trouvée pour le niveau supérieur : " + niveauSup.getNom());
        }

        return classesNiveauSup.get(0);
    }

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
                resultat.getAdmis().size(), resultat.getRedoublants().size(),
                resultat.getExclus().size(), resultat.getDiplomes().size(),
                resultat.getIgnores().size()
        );
    }
}