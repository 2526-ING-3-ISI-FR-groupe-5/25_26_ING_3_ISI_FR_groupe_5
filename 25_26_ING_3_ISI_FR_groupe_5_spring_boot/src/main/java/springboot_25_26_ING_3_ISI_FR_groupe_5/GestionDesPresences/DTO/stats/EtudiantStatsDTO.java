package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.stats;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EtudiantStatsDTO {
    private long nbPresences;
    private long nbRetards;
    private long nbAbsencesNJ;
    private long nbAbsencesJ;
    private double tauxPresence;
    private double totalHeuresPresent;
}