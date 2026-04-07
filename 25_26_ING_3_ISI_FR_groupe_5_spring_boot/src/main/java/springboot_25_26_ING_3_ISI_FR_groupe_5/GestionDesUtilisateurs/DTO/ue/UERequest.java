package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.ue;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UERequest {

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "Le code est obligatoire")
    private String code;

    @NotBlank(message = "Renseignez le libellé en Français")
    private String libelle;

    @NotBlank(message = "Renseignez le libellé en Anglais")
    private String libelleAnglais;

    @NotNull(message = "La spécialité est obligatoire")
    private Long specialiteId;
}