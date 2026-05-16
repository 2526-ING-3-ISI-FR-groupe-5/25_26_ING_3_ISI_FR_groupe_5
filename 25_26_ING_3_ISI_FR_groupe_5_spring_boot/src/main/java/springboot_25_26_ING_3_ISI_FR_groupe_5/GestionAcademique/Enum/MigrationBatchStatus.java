package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Enum;

/**
 * Cycle de vie d'un batch de migration :
 *
 *  EN_EXECUTION ──► TERMINEE ──► PUBLIEE (définitif)
 *                       │
 *                       └──────► ANNULE  (rollback)
 *                  ECHEC (en cas d'erreur)
 */
public enum MigrationBatchStatus {
    EN_EXECUTION,
    TERMINEE,   // migration faite, rollback encore possible
    PUBLIEE,    // définitif, rollback impossible
    ANNULE,     // rollback effectué
    ECHEC
}