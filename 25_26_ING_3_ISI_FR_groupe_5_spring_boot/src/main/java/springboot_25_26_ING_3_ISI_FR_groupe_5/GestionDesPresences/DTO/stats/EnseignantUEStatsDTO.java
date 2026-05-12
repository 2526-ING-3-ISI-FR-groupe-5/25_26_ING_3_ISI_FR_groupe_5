package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.stats;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class EnseignantUEStatsDTO {
    private String ueCode;
    private String ueLibelle;
    private String classeNom;
    private double heuresPrevues;   // ProgrammationUE.dheure
    private double heuresRealisees; // Somme des durées des cours terminés
    private double heuresRestantes;
    private double progression;     // Pourcentage
}