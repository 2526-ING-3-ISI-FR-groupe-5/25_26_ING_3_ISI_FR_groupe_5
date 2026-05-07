package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Enum;

import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesJustificatifs.Entity.Justificatif;


public enum StatutPresence {

    /** Appel pas encore fait pour cet étudiant. */
    EN_ATTENTE,

    /** Étudiant présent à l'heure. */
    PRESENT,

    /**
     * Étudiant arrivé en retard.
     * Applicable uniquement au premier cours du matin (heureDebut ≤ 08:30).
     */
    RETARD,

    /**
     * Étudiant présent une partie du cours seulement
     * (ex : parti avant la fin, arrivé très tard).
     */
    PARTIEL,

    /** Étudiant absent, sans justificatif. */
    ABSENT,

    /** Étudiant absent mais avec justificatif validé. */
    JUSTIFIE
}