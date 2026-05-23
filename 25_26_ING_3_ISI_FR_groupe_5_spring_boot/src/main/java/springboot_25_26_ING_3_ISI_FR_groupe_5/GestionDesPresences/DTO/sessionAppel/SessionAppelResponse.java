package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.sessionAppel;

import lombok.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Enum.MethodeValidation;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Enum.TypeSession;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SessionAppelResponse {

    private Long id;
    private MethodeValidation methode;
    private String code;
    private LocalDateTime dateGeneration;
    private LocalDateTime dateExpiration;
    private boolean actif;
    private boolean expire;
    private boolean coursTermine;
    private TypeSession typeSession;

    // ✅ QR Code base64 pour affichage enseignant
    private String qrCodeBase64;

    // Geolocalisation
    private Double latitudeEnseignant;
    private Double longitudeEnseignant;
    private Integer perimetreMetres;

    // Plage horaire
    private Long plageHoraireId;
    private String plageHoraireTitre;
    private String plageHoraireJour;
    private String plageHoraireHeures;
    private Long nbHeureTotal;

    // Enseignant
    private Long enseignantId;
    private String enseignantNom;
    private String enseignantPrenom;

    // Fin de cours
    private LocalDateTime heureFinReelle;

    // Stats
    private int nbPresents;
    private int nbAbsents;
    private int nbPartiels;
    private int totalEtudiants;
    private int nbRetards;
    private double retardMoyenMinutes;
    private boolean retardAutorise;
    private boolean appelComplet;
    private double tauxPresence;
}