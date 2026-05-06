package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.sessionAppel;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.MethodeValidation;

@Getter
@Setter
public class SessionAppelRequest {

    @NotNull(message = "La plage horaire est obligatoire")
    private Long plageHoraireId;

    @NotNull(message = "La méthode de validation est obligatoire")
    private MethodeValidation methode;

    private Integer dureeMinutes;       // Durée de validité pour QR/PIN (ex: 3)

    // Géolocalisation (récupérée via navigateur PWA)
    private Double latitudeEnseignant;
    private Double longitudeEnseignant;
    private Integer perimetreMetres;    // Rayon en mètres (ex: 50)
}