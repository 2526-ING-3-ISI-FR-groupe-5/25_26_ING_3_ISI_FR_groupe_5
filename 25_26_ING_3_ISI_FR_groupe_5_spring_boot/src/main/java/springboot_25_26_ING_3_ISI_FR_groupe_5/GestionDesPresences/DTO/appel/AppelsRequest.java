package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.appel;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Enum.MethodeValidation;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Enum.StatutPresence;

import java.time.LocalTime;

@Getter
@Setter
public class AppelsRequest {

    @NotNull(message = "L'étudiant est obligatoire")
    private Long etudiantId;

    @NotNull(message = "La plage horaire est obligatoire")
    private Long plageHoraireId;

    private Long enseignantId;

    // Présence
    private boolean present = false;
    private int nbHeuresPresent = 0;

    // Statut explicite (remplace present=true/false)
    private StatutPresence statut;

    /**
     * 🆕 Heure d'arrivée — renseignée uniquement pour les retards
     * (premier cours du matin, heureDebut ≤ 08h30).
     */
    private LocalTime heureArrivee;

    // Méthode
    private MethodeValidation methode;

    // Validation numérique (QR/PIN)
    private String codeSaisi;
    private Long sessionAppelId;

    // Géolocalisation étudiant
    private Double latitudeEtudiant;
    private Double longitudeEtudiant;

    private String commentaire;
}