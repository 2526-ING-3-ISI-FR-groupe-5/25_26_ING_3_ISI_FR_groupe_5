package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences;

import java.time.LocalTime;

public final class PresenceConstants {

    /**
     * Seuil heure de début pour considérer un cours comme "premier cours du matin".
     * Un retard n'est applicable que si heureDebut <= ce seuil.
     */
    public static final LocalTime SEUIL_PREMIER_COURS = LocalTime.of(8, 30);

    private PresenceConstants() {}
}
