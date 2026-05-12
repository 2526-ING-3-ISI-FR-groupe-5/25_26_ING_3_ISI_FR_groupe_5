package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Services.InterfaceService;

import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.stats.EnseignantUEStatsDTO;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.stats.EtudiantStatsDTO;

import java.util.List;

public interface IStatsService {

    // Statistiques étudiant
    EtudiantStatsDTO getStatsEtudiant(Long etudiantId);

    // Progression enseignant
    List<EnseignantUEStatsDTO> getProgressionEnseignant(Long enseignantId);
}
