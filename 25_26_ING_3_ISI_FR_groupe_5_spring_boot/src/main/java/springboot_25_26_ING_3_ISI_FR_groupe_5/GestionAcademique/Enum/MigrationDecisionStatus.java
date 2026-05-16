package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Enum;

public enum MigrationDecisionStatus {
    SELECTIONNEE,     // Choisie dans l'UI
    EN_COURS,         // En cours de clonage
    MIGREE,           // Copiée avec succès en N+1
    ERREUR,           // Échec technique (ex: doublon, FK manquante)
    ANNULEE           // Annulée lors d'un rollback
}