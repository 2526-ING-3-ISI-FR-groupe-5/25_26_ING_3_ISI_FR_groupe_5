package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.programmation;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Classe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Semestre;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.UE;

@Getter
@Setter
public class ProgrammationRequest {

    @NotNull(message = "L'UE est obligatoire")
    private Long ueId;

    @NotNull(message = "Le semestre est obligatoire")
    private Long semestreId;

    @NotNull(message = "La classe est obligatoire")
    private Long classeId;

    private Long dheure;
    private Long nbrCredit;
    private String libelle;
    private String libelleAnglais;

    private Set<Long> enseignantIds;
}