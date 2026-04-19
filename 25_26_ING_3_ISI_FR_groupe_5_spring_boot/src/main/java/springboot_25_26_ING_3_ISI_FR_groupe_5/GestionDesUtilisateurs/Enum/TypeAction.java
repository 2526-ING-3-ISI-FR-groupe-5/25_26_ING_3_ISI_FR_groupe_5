package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum;

public enum TypeAction {

    // ========== Module Core — Authentification ==========
    CONNEXION,
    DECONNEXION,
    TENTATIVE_CONNEXION_ECHOUEE,
    CHANGEMENT_MOT_DE_PASSE,
    COMPTE_ACTIVE,
    COMPTE_DESACTIVE,
    COMPTE_VERROUILLE,

    // ========== Module Core — Utilisateurs ==========
    UTILISATEUR_CREE,
    UTILISATEUR_MODIFIE,
    UTILISATEUR_DESACTIVE,

    // ========== Module Institutionnel — Année académique ==========
    ANNEE_ACADEMIQUE_CREEE,
    ANNEE_ACADEMIQUE_MODIFIEE,
    ANNEE_ACADEMIQUE_ACTIVEE,
    ANNEE_ACADEMIQUE_SUPPRIMEE,

    // ========== Module Institutionnel — Semestre ==========
    SEMESTRE_CREE,
    SEMESTRE_ACTIVE,
    SEMESTRE_DESACTIVE,
    SEMESTRE_SUPPRIME,

    // ========== Module Institutionnel — Ecole / Institut ==========
    ECOLE_CREEE,
    ECOLE_MODIFIEE,
    ECOLE_SUPPRIMEE,
    INSTITUT_CREE,
    INSTITUT_MODIFIE,
    INSTITUT_SUPPRIME,

    // ========== Module Académique — Filière / Cycle ==========
    FILIERE_CREEE,
    FILIERE_MODIFIEE,
    FILIERE_SUPPRIMEE,
    CYCLE_CREE,
    CYCLE_MODIFIE,
    CYCLE_SUPPRIME,

    // ========== Module Académique — UE ==========
    UE_CREEE,
    UE_MODIFIEE,
    UE_SUPPRIMEE,

    // ========== Module Académique — Classe ==========
    CLASSE_CREEE,
    CLASSE_MODIFIEE,
    CLASSE_SUPPRIMEE,

    // ========== Module Académique — Inscription ==========
    INSCRIPTION_CREEE,
    INSCRIPTION_VALIDEE,
    INSCRIPTION_REFUSEE,
    INSCRIPTION_SUPPRIMEE,

    // ========== Module Académique — Migration ==========
    MIGRATION_EFFECTUEE,

    // ========== Module Emploi du temps — Séance ==========
    SEANCE_CREEE,
    SEANCE_MODIFIEE,
    SEANCE_SUPPRIMEE,

    // ========== Module Emploi du temps — Programmation ==========
    PROGRAMMATION_CREEE,
    PROGRAMMATION_MODIFIEE,
    PROGRAMMATION_SUPPRIMEE,

    // ========== Module Appels ==========
    APPEL_LANCE,
    APPEL_CLOTURE,
    PRESENCE_VALIDEE,
    PRESENCE_REFUSEE,
    QR_CODE_GENERE,
    CODE_PIN_GENERE,
    CODE_PIN_UTILISE,

    // ========== Module Appels — Justificatif ==========
    JUSTIFICATIF_SOUMIS,
    JUSTIFICATIF_VALIDE,
    JUSTIFICATIF_REFUSE,
    JUSTIFICATIF_SUPPRIME,

    // ========== Module Notification ==========
    EMAIL_ENVOYE,
    SPECIALITE_SUPPRIMEE,
    NOTIFICATION_ENVOYEE, SPECIALITE_CREEE,SPECIALITE_MODIFIEE,
    NIVEAU_CREE,
    NIVEAU_MODIFIE,
    NIVEAU_SUPPRIME,


}