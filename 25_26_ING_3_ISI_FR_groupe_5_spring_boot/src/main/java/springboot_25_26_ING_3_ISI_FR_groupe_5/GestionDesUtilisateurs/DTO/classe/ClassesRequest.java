package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.classe;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClassesRequest {

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotNull(message = "Le niveau est obligatoire")
    private Long niveauId;

    // ✅ Capacité max ajoutée
    @Min(value = 1, message = "La capacité doit être supérieure à 0")
    private Integer capaciteMax;

    // ✅ Statut actif/inactif
    private boolean active = true;
}