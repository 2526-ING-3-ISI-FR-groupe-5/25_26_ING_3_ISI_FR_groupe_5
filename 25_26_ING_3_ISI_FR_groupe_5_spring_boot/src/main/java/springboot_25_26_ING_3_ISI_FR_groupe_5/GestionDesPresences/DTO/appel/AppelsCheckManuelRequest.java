package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.appel;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;

@Getter
@Setter
public class AppelsCheckManuelRequest {

    @NotNull(message = "La plage horaire est obligatoire")
    private Long plageHoraireId;

    @NotNull(message = "L'enseignant est obligatoire")
    private Long enseignantId;

    /** IDs des étudiants cochés "Présents". */
    private List<Long> etudiantIdsPresents;

    /** Étudiants avec présence partielle. */
    private List<PresencePartielle> presencesPartielles;

    /**
     * 🆕 Étudiants en retard — uniquement pour le premier cours du matin.
     * Le service vérifie la règle métier avant d'appliquer.
     */
    private List<Retard> retards;

    // ── Nested classes ──

    @Getter
    @Setter
    public static class PresencePartielle {
        private Long etudiantId;
        private int nbHeuresPresent;
    }

    @Getter
    @Setter
    public static class Retard {
        private Long etudiantId;

        /**
         * Heure d'arrivée réelle de l'étudiant retardataire.
         * Doit être après l'heure de début du cours.
         */
        private LocalTime heureArrivee;

        private String commentaire;
    }
}