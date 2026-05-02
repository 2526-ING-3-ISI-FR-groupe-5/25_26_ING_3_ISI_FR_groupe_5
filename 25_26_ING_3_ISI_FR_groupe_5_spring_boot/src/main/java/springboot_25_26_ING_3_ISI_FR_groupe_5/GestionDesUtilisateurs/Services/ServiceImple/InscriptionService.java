package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Annee_academique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Classe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Etudiant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Inscription;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.DecisionFinAnnee;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.StatutInscription;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.TypeAction;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.InscriptionRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService.IJournalActionService;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InscriptionService {

    private final InscriptionRepository inscriptionRepo;
    private final EtudiantService etudiantService;
    private final ClassesService classesService;
    private final AnneeAcademiqueService anneeService;
    private final IJournalActionService journalService;

    // ... autres méthodes ...

    // ✅ Méthode corrigée
    @Transactional
    public void enregistrerDecision(Long inscriptionId, DecisionFinAnnee decision, String observations, Utilisateur acteur) {

        Inscription inscription = findById(inscriptionId);

        // Vérifier qu'on est dans l'année active
        Annee_academique anneeActive = anneeService.getAnneeActive();
        if (!inscription.getAnneeAcademique().getId().equals(anneeActive.getId())) {
            throw new RuntimeException("Impossible de modifier une décision d'une année passée");
        }

        inscription.setDecisionFinAnnee(decision);
        inscription.setDateDecision(LocalDate.now());
        inscription.setObservations(observations);

        // Si exclu → désactiver l'étudiant
        if (decision == DecisionFinAnnee.EXCLU) {
            inscription.getEtudiant().setActive(false);
        }

        inscriptionRepo.save(inscription);

        // Journalisation
        journalService.journaliserSucces(acteur, TypeAction.INSCRIPTION_MODIFIEE,
                "Inscription", inscriptionId,
                "Décision enregistrée : " + decision + (observations != null ? " - " + observations : ""));
    }

    public Inscription findById(Long id) {
        return inscriptionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Inscription introuvable"));
    }

    public List<Inscription> getHistoriqueEtudiant(Long id) {
    return null;
    }

    public List<Inscription> getByClasseAndAnnee(Long id, Long id1) {
    return null;}

    public List<Inscription> getEtudiantsActifsByClasse(Long id, Long id1) {
        return null;
    }
}