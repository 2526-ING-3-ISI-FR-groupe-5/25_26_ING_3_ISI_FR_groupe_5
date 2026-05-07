package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.DTO.Cycle;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.TypeCycle;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Cycle;

@Getter
@Setter
public class CycleRequest {

    @NotNull(message = "Le type de cycle est obligatoire")
    private TypeCycle typeCycle;

 
}