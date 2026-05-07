package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.journal.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.JournalAction;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.StatutAction;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.TypeAction;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exception.ResourceNotFoundException;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers.JournalActionMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.JournalActionRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService.IJournalActionService;

import java.time.LocalDateTime;
import java.util.List;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Annee_academique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Institut;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesJustificatifs.Entity.Justificatif;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.Appels;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.journal.IpSuspecteResponse;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.journal.JournalActionResponse;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.journal.JournalEchecResponse;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.journal.JournalStatsResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class JournalActionService implements IJournalActionService {

    private final JournalActionRepository journalActionRepository;
    private final JournalActionMapper journalActionMapper;

    // ============================================
    // Journalisation principale
    // ============================================

// ═══════════════════════════════════════════════════════════
// APPELS (implémentation dans le service)
// ═══════════════════════════════════════════════════════════

    @Override
    @Transactional
    public void journaliserAppel(Utilisateur acteur, Long appelId, String description) {
        journaliser(acteur, TypeAction.APPEL_LANCE, "Appels", appelId, description, StatutAction.SUCCES);
    }

    @Transactional
    public void journaliserLancementAppel(Utilisateur acteur, Long appelId, String plageInfo) {
        journaliser(acteur, TypeAction.APPEL_LANCE, "Appels", appelId,
                "Appel lancé pour : " + plageInfo, StatutAction.SUCCES);
    }

    @Transactional
    public void journaliserClotureAppel(Utilisateur acteur, Long appelId, int nbPresents, int nbAbsents) {
        journaliser(acteur, TypeAction.APPEL_CLOTURE, "Appels", appelId,
                String.format("Appel clôturé : %d présents, %d absents", nbPresents, nbAbsents),
                StatutAction.SUCCES);
    }

    @Transactional
    public void journaliserEchecAppel(Utilisateur acteur, Long appelId, String erreur) {
        journaliser(acteur, TypeAction.APPEL_LANCE, "Appels", appelId,
                "Échec de l'appel : " + erreur, StatutAction.ECHEC);
    }

// ═══════════════════════════════════════════════════════════
// JUSTIFICATIFS (implémentation dans le service)
// ═══════════════════════════════════════════════════════════

    @Override
    @Transactional
    public void journaliserJustificatif(Utilisateur acteur, Long justificatifId, TypeAction type, String description) {
        journaliser(acteur, type, "Justificatif", justificatifId, description, StatutAction.SUCCES);
    }

    @Transactional
    public void journaliserSoumissionJustificatif(Utilisateur acteur, Long justificatifId, String motif) {
        journaliser(acteur, TypeAction.JUSTIFICATIF_SOUMIS, "Justificatif", justificatifId,
                "Justificatif soumis : " + motif, StatutAction.SUCCES);
    }

    @Transactional
    public void journaliserValidationJustificatif(Utilisateur acteur, Long justificatifId) {
        journaliser(acteur, TypeAction.JUSTIFICATIF_VALIDE, "Justificatif", justificatifId,
                "Justificatif validé", StatutAction.SUCCES);
    }

    @Transactional
    public void journaliserRefusJustificatif(Utilisateur acteur, Long justificatifId, String motif) {
        journaliser(acteur, TypeAction.JUSTIFICATIF_REFUSE, "Justificatif", justificatifId,
                "Justificatif refusé : " + motif, StatutAction.SUCCES);
    }

    @Transactional
    public void journaliserSuppressionJustificatif(Utilisateur acteur, Long justificatifId) {
        journaliser(acteur, TypeAction.JUSTIFICATIF_SUPPRIME, "Justificatif", justificatifId,
                "Justificatif supprimé", StatutAction.SUCCES);
    }

    @Override
    @Transactional
    public void journaliser(
            Utilisateur utilisateur,
            TypeAction typeAction,
            String entiteConcernee,
            Long entiteId,
            String description,
            StatutAction statut) {

        try {
            JournalAction journal = JournalAction.builder()
                    .utilisateur(utilisateur)
                    .typeAction(typeAction)
                    .entiteConcernee(entiteConcernee)
                    .entiteId(entiteId)
                    .institut(utilisateur.getInstitut())
                    .description(description)
                    .adresseIp(getClientIp())
                    .navigateur(getUserAgent())
                    .statut(statut)
                    .build();

            journalActionRepository.save(journal);

        } catch (Exception e) {
            // ✅ Ne bloque jamais le flux principal
            log.error("Erreur journalisation : {}", e.getMessage());
        }
    }

    @Override
    @Transactional
    public void journaliserSucces(
            Utilisateur utilisateur,
            TypeAction typeAction,
            String entiteConcernee,
            Long entiteId,
            String description) {

        journaliser(utilisateur, typeAction, entiteConcernee,
                entiteId, description, StatutAction.SUCCES);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void journaliserEchec(
            Utilisateur utilisateur,
            TypeAction typeAction,
            String entiteConcernee,
            Long entiteId,
            String description) {
        try {
            JournalAction journal = JournalAction.builder()
                    .utilisateur(utilisateur)
                    .typeAction(typeAction)
                    .entiteConcernee(entiteConcernee)
                    .entiteId(entiteId)
                    .institut(utilisateur.getInstitut())
                    .description(description)
                    .adresseIp(getClientIp())
                    .navigateur(getUserAgent())
                    .statut(StatutAction.ECHEC)  // ← statut forcé à ECHEC
                    .build();

            journalActionRepository.save(journal);

        } catch (Exception e) {
            log.error("Erreur journalisation échec : {}", e.getMessage());
        }
    }

    // ============================================
    // Consultation — retourne des DTOs ✅
    // ============================================

    @Override
    public JournalActionResponse getById(Long id) {
        JournalAction journal = journalActionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Action introuvable"
                ));
        return journalActionMapper.toResponse(journal);
    }

    @Override
    public Page<JournalActionResponse> getByUtilisateur(
            Long utilisateurId,
            Pageable pageable) {

        return journalActionRepository
                .findByUtilisateurId(utilisateurId, pageable)
                .map(journalActionMapper::toResponse); // ✅ map → DTO
    }

    @Override
    public Page<JournalActionResponse> getByTypeAction(
            TypeAction typeAction,
            Pageable pageable) {

        return journalActionRepository
                .findByTypeAction(typeAction, pageable)
                .map(journalActionMapper::toResponse); // ✅ map → DTO
    }

    @Override
    public Page<JournalActionResponse> search(
            Long utilisateurId,
            TypeAction typeAction,
            StatutAction statut,
            LocalDateTime debut,
            LocalDateTime fin,
            Pageable pageable) {

        if (debut != null && fin != null && debut.isAfter(fin)) {
            throw new IllegalArgumentException(
                    "La date de début ne peut pas être après la date de fin"
            );
        }

        // ✅ Ajout de null comme premier paramètre (institutId)
        return journalActionRepository
                .search(null, utilisateurId, typeAction, statut, debut, fin, pageable)
                .map(journalActionMapper::toResponse);
    }

    // ============================================
    // Statistiques — retourne des DTOs ✅
    // ============================================

    @Override
    public List<JournalStatsResponse> getStatsByType() {
        return journalActionRepository.countByTypeAction()
                .stream()
                .map(journalActionMapper::toStatsResponse) // ✅ map → DTO
                .toList();
    }

    @Override
    public List<JournalEchecResponse> getStatsByEchecs() {
        return journalActionRepository.countEchecByUtilisateur()
                .stream()
                .map(journalActionMapper::toEchecResponse) // ✅ map → DTO
                .toList();
    }

    @Override
    public List<IpSuspecteResponse> getIpsSuspectes(
            LocalDateTime depuis,
            Long seuil) {

        return journalActionRepository.findIpsSuspectes(depuis, seuil)
                .stream()
                .map(journalActionMapper::toIpSuspecteResponse) // ✅ map → DTO
                .toList();
    }

    // ============================================
    // Utilitaires — IP et User-Agent
    // ============================================

    private String getClientIp() {
        try {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder
                            .getRequestAttributes();

            if (attributes == null) return "SYSTEM";

            HttpServletRequest request = attributes.getRequest();

            String ip = request.getHeader("X-Forwarded-For");
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip))
                ip = request.getHeader("Proxy-Client-IP");
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip))
                ip = request.getHeader("WL-Proxy-Client-IP");
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip))
                ip = request.getRemoteAddr();
            if (ip != null && ip.contains(","))
                ip = ip.split(",")[0].trim();

            return ip;

        } catch (Exception e) {
            log.warn("Impossible de récupérer l'IP : {}", e.getMessage());
            return "UNKNOWN";
        }
    }

    private String getUserAgent() {
        try {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder
                            .getRequestAttributes();

            if (attributes == null) return "SYSTEM";

            return attributes.getRequest().getHeader("User-Agent");

        } catch (Exception e) {
            log.warn("Impossible de récupérer le User-Agent : {}", e.getMessage());
            return "UNKNOWN";
        }
    }
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void journaliserDesactivationAnnee(Utilisateur acteur, Long anneeId, String nom) {
        try {
            JournalAction journal = JournalAction.builder()
                    .utilisateur(acteur)
                    .typeAction(TypeAction.ANNEE_ACADEMIQUE_DESACTIVEE)
                    .entiteConcernee("Annee_academique")
                    .entiteId(anneeId)
                    .institut(acteur.getInstitut())
                    .description("Année " + nom + " désactivée")
                    .adresseIp(getClientIp())
                    .navigateur(getUserAgent())
                    .statut(StatutAction.SUCCES)
                    .build();

            journalActionRepository.save(journal);
        } catch (Exception e) {
            log.error("Erreur journalisation désactivation année : {}", e.getMessage());
        }
    }

}