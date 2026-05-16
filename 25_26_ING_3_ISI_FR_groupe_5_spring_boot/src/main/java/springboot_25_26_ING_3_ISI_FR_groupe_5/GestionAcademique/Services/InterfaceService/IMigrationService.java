package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.InterfaceService;

import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.DTO.Migration.MigrationResultat;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;

import java.util.List;

public interface IMigrationService {

    // ═══════════════════════════════════════════════════════════
    // MIGRATION COMPLÈTE
    // ═══════════════════════════════════════════════════════════

    MigrationResultat migrer(Long nouvelleAnneeId, Utilisateur acteur);

    MigrationResultat migrerPourInstitut(Long institutId, Long nouvelleAnneeId, Utilisateur acteur);

    // ═══════════════════════════════════════════════════════════
    // MIGRATIONS SÉLECTIVES MULTI-SÉLECTION
    // Hiérarchie : Institut → École → Filière → Spécialité → Niveau → Classe → Étudiant
    // ═══════════════════════════════════════════════════════════

    MigrationResultat migrerInstituts(List<Long> institutIds, Long nouvelleAnneeId, Utilisateur acteur);

    MigrationResultat migrerEcoles(List<Long> ecoleIds, Long nouvelleAnneeId, Utilisateur acteur);

    MigrationResultat migrerFilieres(List<Long> filiereIds, Long nouvelleAnneeId, Utilisateur acteur);

    MigrationResultat migrerSpecialites(List<Long> specialiteIds, Long nouvelleAnneeId, Utilisateur acteur);

    MigrationResultat migrerNiveaux(List<Long> niveauIds, Long nouvelleAnneeId, Utilisateur acteur);

    MigrationResultat migrerClasses(List<Long> classeIds, Long nouvelleAnneeId, Utilisateur acteur);

    MigrationResultat migrerEtudiants(List<Long> etudiantIds, Long nouvelleAnneeId, Utilisateur acteur);

    // ── Personnels ─────────────────────────────────────────────

    MigrationResultat migrerEnseignants(List<Long> enseignantIds, Long nouvelleAnneeId, Utilisateur acteur);

    MigrationResultat migrerAssistants(List<Long> assistantIds, Long nouvelleAnneeId, Utilisateur acteur);

    // ── Ressources académiques ─────────────────────────────────

    MigrationResultat migrerUEs(List<Long> ueIds, Long nouvelleAnneeId, Utilisateur acteur);

    // ═══════════════════════════════════════════════════════════
    // SIMULATION & VÉRIFICATION
    // ═══════════════════════════════════════════════════════════

    MigrationResultat simuler(Long nouvelleAnneeId);

    MigrationResultat simuler(Long institutId, Long nouvelleAnneeId);

    MigrationResultat simulerPourInstitut(Long institutId, Long nouvelleAnneeId);

    List<String> getEtudiantsSansDecision();

    // ═══════════════════════════════════════════════════════════
    // CYCLE DE VIE DU BATCH
    // ═══════════════════════════════════════════════════════════

    void rollbackMigration(Long batchId, Utilisateur acteur);

    void publierMigration(Long batchId, Utilisateur acteur);

    MigrationResultat migrerEtudiant(Long etudiantId, Long nouvelleAnneeId, Utilisateur acteur);

    MigrationResultat migrerEnseignant(Long enseignantId, Long nouvelleAnneeId, Utilisateur acteur);
}