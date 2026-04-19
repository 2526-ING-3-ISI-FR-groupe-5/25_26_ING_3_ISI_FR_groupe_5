package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService;

import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Classe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Inscription;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.ProgrammationUE;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.StatutInscription;

import java.util.List;
import java.util.Set;

public interface InterfaceProgrammeUE {
    // ══════════════════════════════════════════
    // PROGRAMMER une UE pour une classe/semestre
    // ══════════════════════════════════════════
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

    // ══════════════════════════════════════════
    // RÉCUPÉRER les programmations d'une classe
    // ══════════════════════════════════════════
    @Transactional
    List<ProgrammationUE> getByClasseAndAnnee(Long classeId, Long anneeId);

    // ══════════════════════════════════════════
    // RÉCUPÉRER les programmations d'un enseignant
    // ══════════════════════════════════════════
    @Transactional
    List<ProgrammationUE> getByEnseignantAndAnnee(Long enseignantId, Long anneeId);

    // ══════════════════════════════════════════
    // SUPPRIMER une programmation (seulement si année active)
    // ══════════════════════════════════════════
    @Transactional
    void supprimer(Long id);

    // ══════════════════════════════════════════
    // DUPLIQUER les programmations vers une nouvelle année
    // ══════════════════════════════════════════
    @Transactional
    void dupliquerVersNouvelleAnnee(Long ancienneAnneeId, Long nouvelleAnneeId);

    List<ProgrammationUE> getProgrammationsByEnseignant(Long enseignantId);

    @Transactional
    List<Classe> getClassesByEnseignant(Long enseignantId);

}