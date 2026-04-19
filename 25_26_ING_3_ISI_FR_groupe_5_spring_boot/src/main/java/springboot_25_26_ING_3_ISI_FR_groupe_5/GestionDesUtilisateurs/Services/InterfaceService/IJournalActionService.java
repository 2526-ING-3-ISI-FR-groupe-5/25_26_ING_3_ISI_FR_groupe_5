package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.journal.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.StatutAction;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.TypeAction;

import java.time.LocalDateTime;
import java.util.List;

public interface IJournalActionService {

    // ============================================
    // Méthodes de base — à implémenter
    // ============================================

    void journaliser(
            Utilisateur utilisateur,
            TypeAction typeAction,
            String entiteConcernee,
            Long entiteId,
            String description,
            StatutAction statut
    );

    void journaliserSucces(
            Utilisateur utilisateur,
            TypeAction typeAction,
            String entiteConcernee,
            Long entiteId,
            String description
    );

    void journaliserEchec(
            Utilisateur utilisateur,
            TypeAction typeAction,
            String entiteConcernee,
            Long entiteId,
            String description
    );

    // ============================================
    // Consultation
    // ============================================

    JournalActionResponse getById(Long id);

    Page<JournalActionResponse> getByUtilisateur(
            Long utilisateurId, Pageable pageable);

    Page<JournalActionResponse> getByTypeAction(
            TypeAction typeAction, Pageable pageable);

    Page<JournalActionResponse> search(
            Long utilisateurId, TypeAction typeAction,
            StatutAction statut, LocalDateTime debut,
            LocalDateTime fin, Pageable pageable);

    List<JournalStatsResponse> getStatsByType();
    List<JournalEchecResponse> getStatsByEchecs();
    List<IpSuspecteResponse> getIpsSuspectes(LocalDateTime depuis, Long seuil);

    // ============================================
    // ✅ Méthodes raccourcies — Authentification
    // ============================================

    default void journaliserConnexion(Utilisateur auteur) {
        journaliserSucces(auteur, TypeAction.CONNEXION,
                "Utilisateur", auteur.getId(),
                "Connexion de " + auteur.getEmail());
    }

    default void journaliserDeconnexion(Utilisateur auteur) {
        journaliserSucces(auteur, TypeAction.DECONNEXION,
                "Utilisateur", auteur.getId(),
                "Déconnexion de " + auteur.getEmail());
    }

    default void journaliserTentativeConnexionEchouee(String email, String ip) {
        journaliser(null, TypeAction.TENTATIVE_CONNEXION_ECHOUEE,
                "Utilisateur", null,
                "Tentative échouée pour : " + email,
                StatutAction.ECHEC);
    }

    default void journaliserChangementMotDePasse(Utilisateur auteur) {
        journaliserSucces(auteur, TypeAction.CHANGEMENT_MOT_DE_PASSE,
                "Utilisateur", auteur.getId(),
                "Changement de mot de passe de " + auteur.getEmail());
    }

    // ============================================
    // ✅ Méthodes raccourcies — Compte utilisateur
    // ============================================

    default void journaliserCompteActive(Utilisateur auteur, Long id, String email) {
        journaliserSucces(auteur, TypeAction.COMPTE_ACTIVE,
                "Utilisateur", id,
                "Compte activé : " + email);
    }

    default void journaliserCompteDesactive(Utilisateur auteur, Long id, String email) {
        journaliserSucces(auteur, TypeAction.COMPTE_DESACTIVE,
                "Utilisateur", id,
                "Compte désactivé : " + email);
    }

    default void journaliserCompteVerrouille(Utilisateur auteur, Long id, String email) {
        journaliserSucces(auteur, TypeAction.COMPTE_VERROUILLE,
                "Utilisateur", id,
                "Compte verrouillé : " + email);
    }

    // ============================================
    // ✅ Méthodes raccourcies — Utilisateurs
    // ============================================

    default void journaliserCreationUtilisateur(Utilisateur auteur, Long id, String nom) {
        journaliserSucces(auteur, TypeAction.UTILISATEUR_CREE,
                "Utilisateur", id,
                "Utilisateur créé : " + nom);
    }

    default void journaliserModificationUtilisateur(Utilisateur auteur, Long id, String nom) {
        journaliserSucces(auteur, TypeAction.UTILISATEUR_MODIFIE,
                "Utilisateur", id,
                "Utilisateur modifié : " + nom);
    }

    default void journaliserDesactivationUtilisateur(Utilisateur auteur, Long id, String nom) {
        journaliserSucces(auteur, TypeAction.UTILISATEUR_DESACTIVE,
                "Utilisateur", id,
                "Utilisateur désactivé : " + nom);
    }

    default void journaliserEchecCreationUtilisateur(Utilisateur auteur, String raison) {
        journaliserEchec(auteur, TypeAction.UTILISATEUR_CREE,
                "Utilisateur", null,
                "Echec création utilisateur : " + raison);
    }

    // ============================================
    // ✅ Méthodes raccourcies — Année académique
    // ============================================

    default void journaliserCreationAnnee(Utilisateur auteur, Long id, String nom) {
        journaliserSucces(auteur, TypeAction.ANNEE_ACADEMIQUE_CREEE,
                "Annee_academique", id,
                "Année académique créée : " + nom);
    }

    default void journaliserModificationAnnee(Utilisateur auteur, Long id, String nom) {
        journaliserSucces(auteur, TypeAction.ANNEE_ACADEMIQUE_MODIFIEE,
                "Annee_academique", id,
                "Année académique modifiée : " + nom);
    }

    default void journaliserActivationAnnee(Utilisateur auteur, Long id, String nom) {
        journaliserSucces(auteur, TypeAction.ANNEE_ACADEMIQUE_ACTIVEE,
                "Annee_academique", id,
                "Année académique activée : " + nom);
    }

    default void journaliserSuppressionAnnee(Utilisateur auteur, Long id, String nom) {
        journaliserSucces(auteur, TypeAction.ANNEE_ACADEMIQUE_SUPPRIMEE,
                "Annee_academique", id,
                "Année académique supprimée : " + nom);
    }

    default void journaliserEchecAnnee(Utilisateur auteur, TypeAction typeAction, String raison) {
        journaliserEchec(auteur, typeAction,
                "Annee_academique", null,
                "Echec : " + raison);
    }

    // ============================================
    // ✅ Méthodes raccourcies — Semestre
    // ============================================

    default void journaliserCreationSemestre(Utilisateur auteur, Long id, String libelle) {
        journaliserSucces(auteur, TypeAction.SEMESTRE_CREE,
                "Semestre", id,
                "Semestre créé : " + libelle);
    }

    default void journaliserActivationSemestre(Utilisateur auteur, Long id, String libelle) {
        journaliserSucces(auteur, TypeAction.SEMESTRE_ACTIVE,
                "Semestre", id,
                "Semestre activé : " + libelle);
    }

    default void journaliserDesactivationSemestre(Utilisateur auteur, Long id, String libelle) {
        journaliserSucces(auteur, TypeAction.SEMESTRE_DESACTIVE,
                "Semestre", id,
                "Semestre désactivé : " + libelle);
    }

    default void journaliserSuppressionSemestre(Utilisateur auteur, Long id, String libelle) {
        journaliserSucces(auteur, TypeAction.SEMESTRE_SUPPRIME,
                "Semestre", id,
                "Semestre supprimé : " + libelle);
    }

    default void journaliserEchecSemestre(Utilisateur auteur, TypeAction typeAction, String raison) {
        journaliserEchec(auteur, typeAction,
                "Semestre", null,
                "Echec : " + raison);
    }

    // ============================================
    // ✅ Méthodes raccourcies — École
    // ============================================

    default void journaliserCreationEcole(Utilisateur auteur, Long id, String nom) {
        journaliserSucces(auteur, TypeAction.ECOLE_CREEE,
                "Ecole", id, "École créée : " + nom);
    }

    default void journaliserModificationEcole(Utilisateur auteur, Long id, String nom) {
        journaliserSucces(auteur, TypeAction.ECOLE_MODIFIEE,
                "Ecole", id, "École modifiée : " + nom);
    }

    default void journaliserSuppressionEcole(Utilisateur auteur, Long id, String nom) {
        journaliserSucces(auteur, TypeAction.ECOLE_SUPPRIMEE,
                "Ecole", id, "École supprimée : " + nom);
    }

    // ============================================
    // ✅ Méthodes raccourcies — Institut
    // ============================================

    default void journaliserCreationInstitut(Utilisateur auteur, Long id, String nom) {
        journaliserSucces(auteur, TypeAction.INSTITUT_CREE,
                "Institut", id, "Institut créé : " + nom);
    }

    default void journaliserModificationInstitut(Utilisateur auteur, Long id, String nom) {
        journaliserSucces(auteur, TypeAction.INSTITUT_MODIFIE,
                "Institut", id, "Institut modifié : " + nom);
    }

    default void journaliserSuppressionInstitut(Utilisateur auteur, Long id, String nom) {
        journaliserSucces(auteur, TypeAction.INSTITUT_SUPPRIME,
                "Institut", id, "Institut supprimé : " + nom);
    }

    // ============================================
    // ✅ Méthodes raccourcies — Filière
    // ============================================

    default void journaliserCreationFiliere(Utilisateur auteur, Long id, String nom) {
        journaliserSucces(auteur, TypeAction.FILIERE_CREEE,
                "Filiere", id, "Filière créée : " + nom);
    }

    default void journaliserModificationFiliere(Utilisateur auteur, Long id, String nom) {
        journaliserSucces(auteur, TypeAction.FILIERE_MODIFIEE,
                "Filiere", id, "Filière modifiée : " + nom);
    }

    default void journaliserSuppressionFiliere(Utilisateur auteur, Long id, String nom) {
        journaliserSucces(auteur, TypeAction.FILIERE_SUPPRIMEE,
                "Filiere", id, "Filière supprimée : " + nom);
    }

    // ============================================
    // ✅ Méthodes raccourcies — Cycle
    // ============================================

    default void journaliserCreationCycle(Utilisateur auteur, Long id, String nom) {
        journaliserSucces(auteur, TypeAction.CYCLE_CREE,
                "Cycle", id, "Cycle créé : " + nom);
    }

    default void journaliserModificationCycle(Utilisateur auteur, Long id, String nom) {
        journaliserSucces(auteur, TypeAction.CYCLE_MODIFIE,
                "Cycle", id, "Cycle modifié : " + nom);
    }

    default void journaliserSuppressionCycle(Utilisateur auteur, Long id, String nom) {
        journaliserSucces(auteur, TypeAction.CYCLE_SUPPRIME,
                "Cycle", id, "Cycle supprimé : " + nom);
    }

    // ============================================
    // ✅ Méthodes raccourcies — UE
    // ============================================

    default void journaliserCreationUE(Utilisateur auteur, Long id, String nom) {
        journaliserSucces(auteur, TypeAction.UE_CREEE,
                "UE", id, "UE créée : " + nom);
    }

    default void journaliserModificationUE(Utilisateur auteur, Long id, String nom) {
        journaliserSucces(auteur, TypeAction.UE_MODIFIEE,
                "UE", id, "UE modifiée : " + nom);
    }

    default void journaliserSuppressionUE(Utilisateur auteur, Long id, String nom) {
        journaliserSucces(auteur, TypeAction.UE_SUPPRIMEE,
                "UE", id, "UE supprimée : " + nom);
    }

    // ============================================
    // ✅ Méthodes raccourcies — Classe
    // ============================================

    default void journaliserCreationClasse(Utilisateur auteur, Long id, String nom) {
        journaliserSucces(auteur, TypeAction.CLASSE_CREEE,
                "Classe", id, "Classe créée : " + nom);
    }

    default void journaliserModificationClasse(Utilisateur auteur, Long id, String nom) {
        journaliserSucces(auteur, TypeAction.CLASSE_MODIFIEE,
                "Classe", id, "Classe modifiée : " + nom);
    }

    default void journaliserSuppressionClasse(Utilisateur auteur, Long id, String nom) {
        journaliserSucces(auteur, TypeAction.CLASSE_SUPPRIMEE,
                "Classe", id, "Classe supprimée : " + nom);
    }

    // ============================================
    // ✅ Méthodes raccourcies — Inscription
    // ============================================

    default void journaliserCreationInscription(Utilisateur auteur, Long id, String etudiant) {
        journaliserSucces(auteur, TypeAction.INSCRIPTION_CREEE,
                "Inscription", id, "Inscription créée pour : " + etudiant);
    }

    default void journaliserValidationInscription(Utilisateur auteur, Long id, String etudiant) {
        journaliserSucces(auteur, TypeAction.INSCRIPTION_VALIDEE,
                "Inscription", id, "Inscription validée pour : " + etudiant);
    }

    default void journaliserRefusInscription(Utilisateur auteur, Long id, String etudiant) {
        journaliserSucces(auteur, TypeAction.INSCRIPTION_REFUSEE,
                "Inscription", id, "Inscription refusée pour : " + etudiant);
    }

    default void journaliserSuppressionInscription(Utilisateur auteur, Long id, String etudiant) {
        journaliserSucces(auteur, TypeAction.INSCRIPTION_SUPPRIMEE,
                "Inscription", id, "Inscription supprimée pour : " + etudiant);
    }

    // ============================================
    // ✅ Méthodes raccourcies — Séance de cours
    // ============================================

    default void journaliserCreationSeance(Utilisateur auteur, Long id, String libelle) {
        journaliserSucces(auteur, TypeAction.SEANCE_CREEE,
                "SeanceCours", id, "Séance créée : " + libelle);
    }

    default void journaliserModificationSeance(Utilisateur auteur, Long id, String libelle) {
        journaliserSucces(auteur, TypeAction.SEANCE_MODIFIEE,
                "SeanceCours", id, "Séance modifiée : " + libelle);
    }

    default void journaliserSuppressionSeance(Utilisateur auteur, Long id, String libelle) {
        journaliserSucces(auteur, TypeAction.SEANCE_SUPPRIMEE,
                "SeanceCours", id, "Séance supprimée : " + libelle);
    }

    // ============================================
    // ✅ Méthodes raccourcies — Programmation UE
    // ============================================

    default void journaliserCreationProgrammation(Utilisateur auteur, Long id, String libelle) {
        journaliserSucces(auteur, TypeAction.PROGRAMMATION_CREEE,
                "ProgrammationUE", id, "Programmation créée : " + libelle);
    }

    default void journaliserModificationProgrammation(Utilisateur auteur, Long id, String libelle) {
        journaliserSucces(auteur, TypeAction.PROGRAMMATION_MODIFIEE,
                "ProgrammationUE", id, "Programmation modifiée : " + libelle);
    }

    default void journaliserSuppressionProgrammation(Utilisateur auteur, Long id, String libelle) {
        journaliserSucces(auteur, TypeAction.PROGRAMMATION_SUPPRIMEE,
                "ProgrammationUE", id, "Programmation supprimée : " + libelle);
    }

    // ============================================
    // ✅ Méthodes raccourcies — Appels
    // ============================================

    default void journaliserLancementAppel(Utilisateur auteur, Long id, String seance) {
        journaliserSucces(auteur, TypeAction.APPEL_LANCE,
                "Appels", id, "Appel lancé pour : " + seance);
    }

    default void journaliserClotureAppel(Utilisateur auteur, Long id, String seance) {
        journaliserSucces(auteur, TypeAction.APPEL_CLOTURE,
                "Appels", id, "Appel clôturé pour : " + seance);
    }

    default void journaliserPresenceValidee(Utilisateur auteur, Long id, String etudiant) {
        journaliserSucces(auteur, TypeAction.PRESENCE_VALIDEE,
                "Appels", id, "Présence validée pour : " + etudiant);
    }

    default void journaliserPresenceRefusee(Utilisateur auteur, Long id, String etudiant) {
        journaliserSucces(auteur, TypeAction.PRESENCE_REFUSEE,
                "Appels", id, "Présence refusée pour : " + etudiant);
    }

    default void journaliserGenerationQrCode(Utilisateur auteur, Long id, String seance) {
        journaliserSucces(auteur, TypeAction.QR_CODE_GENERE,
                "Appels", id, "QR Code généré pour : " + seance);
    }

    default void journaliserGenerationCodePin(Utilisateur auteur, Long id, String seance) {
        journaliserSucces(auteur, TypeAction.CODE_PIN_GENERE,
                "Appels", id, "Code PIN généré pour : " + seance);
    }

    default void journaliserUtilisationCodePin(Utilisateur auteur, Long id, String etudiant) {
        journaliserSucces(auteur, TypeAction.CODE_PIN_UTILISE,
                "Appels", id, "Code PIN utilisé par : " + etudiant);
    }

    // ============================================
    // ✅ Méthodes raccourcies — Justificatif
    // ============================================

    default void journaliserSoumissionJustificatif(Utilisateur auteur, Long id, String etudiant) {
        journaliserSucces(auteur, TypeAction.JUSTIFICATIF_SOUMIS,
                "Justificatif", id, "Justificatif soumis par : " + etudiant);
    }

    default void journaliserValidationJustificatif(Utilisateur auteur, Long id, String etudiant) {
        journaliserSucces(auteur, TypeAction.JUSTIFICATIF_VALIDE,
                "Justificatif", id, "Justificatif validé pour : " + etudiant);
    }

    default void journaliserRefusJustificatif(Utilisateur auteur, Long id, String etudiant) {
        journaliserSucces(auteur, TypeAction.JUSTIFICATIF_REFUSE,
                "Justificatif", id, "Justificatif refusé pour : " + etudiant);
    }

    default void journaliserSuppressionJustificatif(Utilisateur auteur, Long id, String etudiant) {
        journaliserSucces(auteur, TypeAction.JUSTIFICATIF_SUPPRIME,
                "Justificatif", id, "Justificatif supprimé pour : " + etudiant);
    }

    // ============================================
    // ✅ Méthodes raccourcies — Notification
    // ============================================

    default void journaliserEnvoiEmail(Utilisateur auteur, Long id, String destinataire) {
        journaliserSucces(auteur, TypeAction.EMAIL_ENVOYE,
                "Email", id, "Email envoyé à : " + destinataire);
    }

    default void journaliserEnvoiNotification(Utilisateur auteur, Long id, String destinataire) {
        journaliserSucces(auteur, TypeAction.NOTIFICATION_ENVOYEE,
                "Notification", id, "Notification envoyée à : " + destinataire);
    }

    // ============================================
    // ✅ Méthodes raccourcies — Migration
    // ============================================

    default void journaliserMigration(Utilisateur auteur, Long id, String description) {
        journaliserSucces(auteur, TypeAction.MIGRATION_EFFECTUEE,
                "Migration", id, "Migration effectuée : " + description);
    }

// ============================================
// ✅ Méthodes raccourcies — Spécialité
// ============================================

    default void journaliserCreationSpecialite(Utilisateur acteur, Long id, String nom) {
        journaliserSucces(acteur, TypeAction.SPECIALITE_CREEE,
                "Specialite", id, "Spécialité créée : " + nom);
    }

    default void journaliserModificationSpecialite(Utilisateur acteur, Long id, String nom) {
        journaliserSucces(acteur, TypeAction.SPECIALITE_MODIFIEE,
                "Specialite", id, "Spécialité modifiée : " + nom);
    }

    default void journaliserSuppressionSpecialite(Utilisateur acteur, Long id, String nom) {
        journaliserSucces(acteur, TypeAction.SPECIALITE_SUPPRIMEE,
                "Specialite", id, "Spécialité supprimée : " + nom);
    }
}