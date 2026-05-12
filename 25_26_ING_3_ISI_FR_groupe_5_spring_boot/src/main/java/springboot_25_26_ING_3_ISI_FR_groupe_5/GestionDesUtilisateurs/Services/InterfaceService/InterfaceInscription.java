package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.inscription.DecisionHistory;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Inscription;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.StatutInscription;

import java.util.List;

public interface InterfaceInscription {

    @Transactional
    Inscription inscrire(Long etudiantId, Long classeId, Long anneeId);

    @Transactional
    Inscription changerStatut(Long inscriptionId, StatutInscription statut);

    @Transactional
    Inscription enregistrerDecision(Long inscriptionId, String decision);


    List<Inscription> getByClasseAndAnnee(Long classeId, Long anneeId);



    Page<Inscription> getByClasseAndAnneePaginated(
            Long classeId,
            Long anneeId,
            Pageable pageable
    );


    List<Inscription> getActifsByClasseAndAnnee(Long classeId, Long anneeId);

    List<Inscription> getHistoriqueEtudiant(Long etudiantId);

    Inscription findById(Long id);

    List<DecisionHistory> getDecisionHistory(Long id);
}

