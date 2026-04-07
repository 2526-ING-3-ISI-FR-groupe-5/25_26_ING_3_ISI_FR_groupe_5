package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Annee_academique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Classe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Etudiant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Inscription;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enums.StatutInscription;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.InscriptionRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService.InterfaceInscription;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InscriptionService implements InterfaceInscription {

    private final InscriptionRepository inscriptionRepo;
    private final EtudiantService etudiantService;
    private final ClassesService classesService;
    private final AnneeAcademiqueService anneeService;
@Transactional
@Override
public Inscription inscrire(Long etudiantId, Long classeId, Long anneeId) {

        Etudiant etudiant = etudiantService.findById(etudiantId);
        Classe classe = classesService.findById(classeId);
        Annee_academique annee = anneeService.findById(anneeId);

        if (inscriptionRepo.existsByEtudiantIdAndAnneeAcademiqueId(etudiantId, anneeId)) {
            throw new RuntimeException(
                    "Cet étudiant est déjà inscrit pour cette année académique"
            );
        }

        Inscription inscription = Inscription.builder()
                .etudiant(etudiant)
                .classe(classe)
                .anneeAcademique(annee)
                .statut(StatutInscription.ACTIF) // ✅ ACTIF par défaut
                .build();

        return inscriptionRepo.save(inscription);
    }

    // ══════════════════════════════════════════
    // CHANGER LE STATUT
    // ══════════════════════════════════════════
    @Transactional
    public Inscription changerStatut(Long inscriptionId, StatutInscription statut) {

        Inscription inscription = findById(inscriptionId);

        // Vérifier qu'on est dans l'année active
        Annee_academique anneeActive = anneeService.getAnneeActive();
        if (!inscription.getAnneeAcademique().getId().equals(anneeActive.getId())) {
            throw new RuntimeException(
                    "Impossible de modifier le statut d'une inscription d'une année passée"
            );
        }

        inscription.setStatut(statut);
        return inscriptionRepo.save(inscription);
    }

    // ══════════════════════════════════════════
    // ENREGISTRER LA DÉCISION DE FIN D'ANNÉE
    // ══════════════════════════════════════════
    @Transactional
    public Inscription enregistrerDecision(Long inscriptionId, String decision) {

        List<String> decisionsValides = List.of("ADMIS", "REDOUBLANT", "EXCLU", "DIPLOME");
        if (!decisionsValides.contains(decision)) {
            throw new RuntimeException(
                    "Décision invalide. Valeurs acceptées : " + decisionsValides
            );
        }

        Inscription inscription = findById(inscriptionId);

        Annee_academique anneeActive = anneeService.getAnneeActive();
        if (!inscription.getAnneeAcademique().getId().equals(anneeActive.getId())) {
            throw new RuntimeException(
                    "Impossible de modifier une décision d'une année passée"
            );
        }

        inscription.setDecisionFinAnnee(decision);

        // ✅ Si exclu → suspendre automatiquement
        if ("EXCLU".equals(decision)) {
            inscription.setStatut(StatutInscription.ABANDONNE);
        }

        return inscriptionRepo.save(inscription);
    }

    // ══════════════════════════════════════════
    // RÉCUPÉRER par classe + année
    // ══════════════════════════════════════════
    public List<Inscription> getByClasseAndAnnee(Long classeId, Long anneeId) {
        return inscriptionRepo.findByClasseIdAndAnneeAcademiqueId(classeId, anneeId);
    }

    // ✅ Uniquement les actifs
    public List<Inscription> getActifsByClasseAndAnnee(Long classeId, Long anneeId) {
        return inscriptionRepo.findByClasseIdAndAnneeAcademiqueIdAndStatut(
                classeId, anneeId, StatutInscription.ACTIF
        );
    }

    // ══════════════════════════════════════════
    // HISTORIQUE d'un étudiant
    // ══════════════════════════════════════════
    public List<Inscription> getHistoriqueEtudiant(Long etudiantId) {
        etudiantService.findById(etudiantId);
        return inscriptionRepo.findByEtudiantId(etudiantId);
    }

    public Inscription findById(Long id) {
        return inscriptionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Inscription introuvable"));
    }
}
