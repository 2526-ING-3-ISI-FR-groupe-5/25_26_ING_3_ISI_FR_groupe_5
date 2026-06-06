package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesJustificatifs.Services.ServiceImple;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.Appels;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesJustificatifs.Entity.Justificatif;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesJustificatifs.Enum.StatutJustificatif;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesJustificatifs.DTO.justificatif.JustificatifRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Enum.StatutPresence;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.TypeAction;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exception.ResourceNotFoundException;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Repository.AppelsRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesJustificatifs.Repository.JustificatifRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService.IJournalActionService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesJustificatifs.Services.InterfaceService.IJustificatifService;

import java.time.LocalDateTime;
import java.util.List;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Etudiant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.EtudiantService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.JournalActionService;

@Slf4j
@Service
@RequiredArgsConstructor
public class JustificatifService implements IJustificatifService {

    private final JustificatifRepository justificatifRepository;
    private final AppelsRepository appelsRepository;
    private final EtudiantService etudiantService;
    private final IJournalActionService journalActionService;

    // ============================================
    // Consultation
    // ============================================

    @Override
    @Transactional(readOnly = true)
    public Justificatif findById(Long id) {
        return justificatifRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Justificatif introuvable : " + id
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Justificatif> getByEtudiant(Long etudiantId) {
        return justificatifRepository
                .findByEtudiantIdOrderByDateSoumissionDesc(etudiantId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Justificatif> getEnAttenteByClasse(Long classeId) {
        return justificatifRepository.findEnAttenteByClasse(classeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Justificatif> getEnAttenteByInstitut(Long institutId) {
        return justificatifRepository.findEnAttenteByInstitut(institutId);
    }

    // ============================================
    // Soumettre un justificatif
    // ============================================

    @Override
    @Transactional
    public Justificatif soumettre(
            JustificatifRequest req,
            Utilisateur auteur) {

        // 1. Vérifier cohérence des dates
        if (req.getDateDebutAbsence() != null
                && req.getDateFinAbsence() != null
                && req.getDateFinAbsence()
                .isBefore(req.getDateDebutAbsence())) {
            throw new IllegalArgumentException(
                    "La date de fin ne peut pas être avant la date de début"
            );
        }

        // 2. ✅ Vérifier doublon — même étudiant, mêmes dates
        if (req.getDateDebutAbsence() != null) {
            boolean doublon = justificatifRepository
                    .existsByEtudiantIdAndDateDebutAbsence(
                            req.getEtudiantId(),
                            req.getDateDebutAbsence()
                    );
            if (doublon) {
                throw new IllegalStateException(
                        "Un justificatif existe déjà pour ces dates"
                );
            }
        }

        Justificatif j = Justificatif.builder()
                .etudiant(etudiantService.findById(req.getEtudiantId()))
                .contenu(req.getContenu())
                .fichierUrl(req.getFichierUrl())
                .dateDebutAbsence(req.getDateDebutAbsence())
                .dateFinAbsence(req.getDateFinAbsence())
                .nombreHeures(req.getNombreHeures())
                .type(req.getType())
                .statut(StatutJustificatif.EN_ATTENTE)
                .dateSoumission(LocalDateTime.now())
                .build();

        justificatifRepository.save(j);

        // ✅ Journaliser
        journalActionService.journaliserSoumissionJustificatif(
                auteur,
                j.getId(),
                j.getEtudiant().getNom() + " " + j.getEtudiant().getPrenom()
        );

        log.info("✅ Justificatif soumis par : {}",
                j.getEtudiant().getEmail());

        return j;
    }

    // ============================================
    // Valider un justificatif
    // ============================================

    @Override
    @Transactional
    public Justificatif valider(
            Long id,
            Utilisateur validateur,
            String commentaire) {

        Justificatif j = findById(id);

        // 1. Vérifier statut
        if (!j.isEnAttente()) {
            throw new IllegalStateException(
                    "Ce justificatif a déjà été traité — statut : "
                            + j.getStatut()
            );
        }

        // 2. Mettre à jour le justificatif
        j.setStatut(StatutJustificatif.VALIDE);
        j.setValidateur(validateur);
        j.setDateValidation(LocalDateTime.now());
        if (commentaire != null) j.setCommentaireValidation(commentaire);

        // 3. ✅ Mise à jour bulk des appels — une seule requête
        appelsRepository.updateStatutByJustificatifId(
                id, StatutPresence.JUSTIFIE
        );

        justificatifRepository.save(j);

        // ✅ Journaliser
        journalActionService.journaliserValidationJustificatif(
                validateur,
                j.getId(),
                j.getEtudiant().getNom() + " " + j.getEtudiant().getPrenom()
        );

        log.info("✅ Justificatif {} validé par : {}",
                id, validateur.getEmail());

        return j;
    }

    // ============================================
    // Refuser un justificatif
    // ============================================

    @Override
    @Transactional
    public Justificatif refuser(
            Long id,
            Utilisateur validateur,
            String commentaire) {

        Justificatif j = findById(id);

        // 1. Vérifier statut
        if (!j.isEnAttente()) {
            throw new IllegalStateException(
                    "Ce justificatif a déjà été traité — statut : "
                            + j.getStatut()
            );
        }

        // 2. Mettre à jour le justificatif
        j.setStatut(StatutJustificatif.REFUSE);
        j.setValidateur(validateur);
        j.setDateValidation(LocalDateTime.now());
        if (commentaire != null) j.setCommentaireValidation(commentaire);

        // Les appels restent ABSENT — pas de changement
        justificatifRepository.save(j);

        // ✅ Journaliser
        journalActionService.journaliserRefusJustificatif(
                validateur,
                j.getId(),
                j.getEtudiant().getNom() + " " + j.getEtudiant().getPrenom()
        );

        log.info("❌ Justificatif {} refusé par : {}",
                id, validateur.getEmail());

        return j;
    }

    // ============================================
    // Lier des appels au justificatif
    // ============================================

    @Override
    @Transactional
    public Justificatif lierAppels(
            Long justificatifId,
            List<Long> appelIds) {

        Justificatif j = findById(justificatifId);

        appelIds.forEach(appelId -> {
            Appels appel = appelsRepository.findById(appelId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Appel introuvable : " + appelId
                    ));

            // ✅ Vérifier doublon avant d'ajouter
            if (!j.getAppels().contains(appel)) {
                j.addAppel(appel);
            }
        });

        return justificatifRepository.save(j);
    }

    // ============================================
    // Supprimer un justificatif
    // ============================================

    @Override
    @Transactional
    public void supprimer(Long id, Utilisateur auteur) {
        Justificatif j = findById(id);

        // Ownership — un étudiant ne peut supprimer que ses propres justificatifs.
        // Admin & assistant : pas de restriction (déjà gardés par @PreAuthorize côté controller).
        if (auteur instanceof Etudiant) {
            if (j.getEtudiant() == null
                    || !auteur.getId().equals(j.getEtudiant().getId())) {
                throw new AccessDeniedException(
                        "Vous ne pouvez supprimer que vos propres justificatifs."
                );
            }
        }

        // Vérifier que le justificatif n'est pas validé
        if (j.isValide()) {
            throw new IllegalStateException(
                    "Impossible de supprimer un justificatif validé"
            );
        }

        justificatifRepository.delete(j);

        // ✅ Journaliser
        journalActionService.journaliserSuppressionJustificatif(
                auteur,
                id,
                j.getEtudiant().getNom() + " " + j.getEtudiant().getPrenom()
        );

        log.info("🗑️ Justificatif {} supprimé par : {}",
                id, auteur.getEmail());
    }


}