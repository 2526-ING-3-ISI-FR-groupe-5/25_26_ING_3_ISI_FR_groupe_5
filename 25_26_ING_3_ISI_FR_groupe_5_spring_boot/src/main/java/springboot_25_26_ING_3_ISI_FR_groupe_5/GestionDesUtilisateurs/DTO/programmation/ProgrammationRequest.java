package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.programmation;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProgrammationRequest {

    @NotNull(message = "L'UE est obligatoire")
    private Long ueId;

    @NotNull(message = "Le semestre est obligatoire")
    private Long semestreId;

    @NotNull(message = "La classe est obligatoire")
    private Long classeId;

    @NotNull(message = "Le quota horaire est obligatoire")
    @Min(value = 1, message = "Le quota horaire doit être supérieur à 0")
    private Long dheure;

    @NotNull(message = "Le nombre de crédits est obligatoire")
    @Min(value = 1, message = "Le nombre de crédits doit être supérieur à 0")
    private Long nbrCredit;

    private Set<Long> enseignantIds = new HashSet<>();
}
