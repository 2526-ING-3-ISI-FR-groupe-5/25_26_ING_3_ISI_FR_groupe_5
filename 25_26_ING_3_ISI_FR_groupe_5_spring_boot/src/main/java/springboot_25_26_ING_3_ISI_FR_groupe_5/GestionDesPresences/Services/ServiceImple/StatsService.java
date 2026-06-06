package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Services.ServiceImple;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple.AnneeAcademiqueService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple.InstitutSecurityService;
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
    private final AnneeAcademiqueService anneeService;
    private final InstitutSecurityService securityService;

    // ═══════════════════════════════════════════════════════════
    // STATS ÉTUDIANT
    // ═══════════════════════════════════════════════════════════

    /**
     * Stats de l'étudiant pour le semestre actif.
     * Conservé pour compatibilité avec le dashboard enseignant.
     */
    @Override
    public EtudiantStatsDTO getStatsEtudiant(Long etudiantId) {
        long presences = appelsRepository.countByEtudiantAndStatutAndSemestreActif(etudiantId, StatutPresence.PRESENT);
        long retards   = appelsRepository.countByEtudiantAndStatutAndSemestreActif(etudiantId, StatutPresence.RETARD);
        long absJ      = appelsRepository.countByEtudiantAndStatutAndSemestreActif(etudiantId, StatutPresence.JUSTIFIE);
        long absNJ     = appelsRepository.countAbsencesNonJustifieesByEtudiant(etudiantId);

        return buildStats(presences, retards, absJ, absNJ);
    }

    /**
     * ✅ AJOUTÉ — Stats de l'étudiant pour une année précise.
     * Permet de consulter N-1, N-2... sans être limité au semestre actif.
     *
     * @param etudiantId identifiant de l'étudiant
     * @param anneeId    identifiant de l'année académique cible
     *                   (null → délègue à getStatsEtudiant pour le semestre actif)
     */
    public EtudiantStatsDTO getStatsEtudiantParAnnee(Long etudiantId, Long anneeId) {
        if (anneeId == null) return getStatsEtudiant(etudiantId);

        long presences = appelsRepository.countByEtudiantAndStatutAndAnnee(etudiantId, StatutPresence.PRESENT, anneeId);
        long retards   = appelsRepository.countByEtudiantAndStatutAndAnnee(etudiantId, StatutPresence.RETARD, anneeId);
        long absJ      = appelsRepository.countByEtudiantAndStatutAndAnnee(etudiantId, StatutPresence.JUSTIFIE, anneeId);
        long absNJ     = appelsRepository.countAbsencesNonJustifieesByEtudiantAndAnnee(etudiantId, anneeId);

        return buildStats(presences, retards, absJ, absNJ);
    }

    /**
     * ✅ AJOUTÉ — Récap des heures de présence d'un étudiant pour une année précise.
     * Utile pour afficher le total d'heures validées sur N-1 après un rollback.
     */
    public int getHeuresPresentParAnnee(Long etudiantId, Long anneeId) {
        return appelsRepository.sumHeuresPresentByEtudiantAndAnnee(etudiantId, anneeId);
    }

    // ═══════════════════════════════════════════════════════════
    // PROGRESSION ENSEIGNANT
    // ═══════════════════════════════════════════════════════════

    /**
     * Progression de l'enseignant pour l'année académique active.
     */
    @Override
    public List<EnseignantUEStatsDTO> getProgressionEnseignant(Long enseignantId) {
        Long anneeActiveId = anneeService.getAnneeActive().getId();
        List<ProgrammationUE> programmations =
                programmationRepository.findByEnseignantIdAndAnneeId(enseignantId, anneeActiveId);
        return buildProgression(programmations);
    }

    /**
     * ✅ AJOUTÉ — Progression de l'enseignant pour une année précise.
     * Permet de consulter N-1, N-2... sans être limité au semestre actif.
     *
     * @param enseignantId identifiant de l'enseignant
     * @param anneeId      identifiant de l'année académique cible
     *                     (null → délègue à getProgressionEnseignant)
     */
    public List<EnseignantUEStatsDTO> getProgressionEnseignantParAnnee(Long enseignantId, Long anneeId) {
        if (anneeId == null) return getProgressionEnseignant(enseignantId);

        List<ProgrammationUE> programmations =
                programmationRepository.findByEnseignantIdAndAnneeId(enseignantId, anneeId);
        return buildProgression(programmations);
    }

    // ═══════════════════════════════════════════════════════════
    // PRIVÉ — Builders partagés
    // ═══════════════════════════════════════════════════════════

    private EtudiantStatsDTO buildStats(long presences, long retards, long absJ, long absNJ) {
        long totalPoints = presences + retards + absJ + absNJ;
        double taux = (totalPoints == 0) ? 100.0
                : ((double) (presences + retards) / totalPoints) * 100;

        return EtudiantStatsDTO.builder()
                .nbPresences(presences)
                .nbRetards(retards)
                .nbAbsencesJ(absJ)
                .nbAbsencesNJ(absNJ)
                .tauxPresence(Math.round(taux * 10.0) / 10.0)
                .build();
    }

    private List<EnseignantUEStatsDTO> buildProgression(List<ProgrammationUE> programmations) {
        return programmations.stream().map(prog -> {
            List<PlageHoraire> plagesTerminees =
                    plageHoraireRepository.findPlagesTermineesByProgrammation(prog.getId());

            double heuresFaites = plagesTerminees.stream()
                    .mapToDouble(PlageHoraire::getDureeHeures)
                    .sum();

            double heuresPrevues = (prog.getDheure() != null) ? prog.getDheure() : 0.0;
            double pourcentage   = (heuresPrevues == 0) ? 0 : (heuresFaites / heuresPrevues) * 100;

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