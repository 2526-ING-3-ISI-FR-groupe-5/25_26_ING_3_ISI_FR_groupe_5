package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Services.InterfaceService;

import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Classe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.ProgrammationUE;

import java.util.List;
import java.util.Set;

public interface IProgrammationUEService {

    @Transactional
    ProgrammationUE programmer(
            Long ueId,
            Long semestreId,
            Long classeId,
            Long dheure,
            Long nbrCredit,
            Set<Long> enseignantIds
    );

    @Transactional
    ProgrammationUE modifier(
            Long id,
            Long dheure,
            Long nbrCredit,
            Set<Long> enseignantIds,
            String libelle,
            String libelleAnglais
    );

    @Transactional
    List<ProgrammationUE> getByClasseAndAnnee(Long classeId, Long anneeId);

    @Transactional
    List<ProgrammationUE> getByEnseignantAndAnnee(Long enseignantId, Long anneeId);

    @Transactional
    void supprimer(Long id);

    @Transactional
    void dupliquerVersNouvelleAnnee(Long ancienneAnneeId, Long nouvelleAnneeId);

    @Transactional
    void dupliquerEnseignantVersNouvelleAnnee(Long enseignantId, Long ancienneAnneeId, Long nouvelleAnneeId);

    @Transactional(readOnly = true)
    ProgrammationUE findById(Long id);

    List<ProgrammationUE> getProgrammationsByEnseignant(Long enseignantId);

    @Transactional
    List<Classe> getClassesByEnseignant(Long enseignantId);

    void dupliquerUEVersNouvelleAnnee(Long ueId, Long id, Long id1);
}
