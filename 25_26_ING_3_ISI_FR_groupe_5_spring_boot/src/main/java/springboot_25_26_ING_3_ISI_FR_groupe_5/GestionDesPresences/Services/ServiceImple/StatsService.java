package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Services.ServiceImple;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.stats.EnseignantUEStatsDTO;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.stats.EtudiantStatsDTO;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Services.InterfaceService.IStatsService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.PlageHoraire;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.ProgrammationUE;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Enum.StatutPresence;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Repository.AppelsRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Repository.PlageHoraireRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Repository.ProgrammationUERepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsService implements IStatsService {

    private final AppelsRepository appelsRepository;
    private final ProgrammationUERepository programmationRepository;
    private final PlageHoraireRepository plageHoraireRepository;

    // ══════════════════════════════════════════
    // STATS ÉTUDIANT (Semestre Actif)
    // ══════════════════════════════════════════
    public EtudiantStatsDTO getStatsEtudiant(Long etudiantId) {
        long presences = appelsRepository.countByEtudiantAndStatutAndSemestreActif(etudiantId, StatutPresence.PRESENT);
        long retards = appelsRepository.countByEtudiantAndStatutAndSemestreActif(etudiantId, StatutPresence.RETARD);
        long absJ = appelsRepository.countByEtudiantAndStatutAndSemestreActif(etudiantId, StatutPresence.JUSTIFIE);
        long absNJ = appelsRepository.countAbsencesNonJustifieesByEtudiant(etudiantId);

        long totalPoints = presences + retards + absJ + absNJ;
        double taux = (totalPoints == 0) ? 100.0 : ((double) (presences + retards) / totalPoints) * 100;

        return EtudiantStatsDTO.builder()
                .nbPresences(presences)
                .nbRetards(retards)
                .nbAbsencesJ(absJ)
                .nbAbsencesNJ(absNJ)
                .tauxPresence(Math.round(taux * 10.0) / 10.0)
                .build();
    }

    // ══════════════════════════════════════════
    // PROGRESSION ENSEIGNANT (Par UE/Programmation)
    // ══════════════════════════════════════════
    public List<EnseignantUEStatsDTO> getProgressionEnseignant(Long enseignantId) {
        // Utilise ta méthode findByEnseignantsIdAndSemestreActifTrue du repo
        List<ProgrammationUE> programmations = programmationRepository.findByEnseignantsIdAndSemestreActifTrue(enseignantId);

        return programmations.stream().map(prog -> {
            // Récupérer les plages terminées pour cette programmation spécifique
            List<PlageHoraire> plagesTerminees = plageHoraireRepository.findPlagesTermineesByProgrammation(prog.getId());

            // Somme des durées via ton helper Java getDureeHeures()
            double heuresFaites = plagesTerminees.stream()
                    .mapToDouble(PlageHoraire::getDureeHeures)
                    .sum();

            double heuresPrevues = (prog.getDheure() != null) ? prog.getDheure() : 0.0;
            double pourcentage = (heuresPrevues == 0) ? 0 : (heuresFaites / heuresPrevues) * 100;

            return EnseignantUEStatsDTO.builder()
                    .ueCode(prog.getUe().getCode())
                    .ueLibelle(prog.getUe().getNom())
                    .classeNom(prog.getClasse().getNom())
                    .heuresPrevues(heuresPrevues)
                    .heuresRealisees(heuresFaites)
                    .heuresRestantes(Math.max(0, heuresPrevues - heuresFaites))
                    .progression(Math.round(pourcentage * 10.0) / 10.0)
                    .build();
        }).collect(Collectors.toList());
    }
}