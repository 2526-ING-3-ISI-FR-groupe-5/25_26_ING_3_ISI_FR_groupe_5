package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.DTO.classes;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Niveau;

@Getter
@Setter
public class ClassesRequest {

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotNull(message = "Le niveau est obligatoire")
    private Long niveauId;
}