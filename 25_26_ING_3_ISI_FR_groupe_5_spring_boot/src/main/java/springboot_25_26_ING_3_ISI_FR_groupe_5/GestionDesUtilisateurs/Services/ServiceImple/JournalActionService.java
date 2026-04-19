package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.journal.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.JournalAction;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.StatutAction;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.TypeAction;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exceptions.ResourceNotFoundException;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers.JournalActionMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.JournalActionRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService.IJournalActionService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JournalActionService implements IJournalActionService {

    private final JournalActionRepository journalActionRepository;
    private final JournalActionMapper journalActionMapper;

    // ============================================
    // Journalisation principale
    // ============================================

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
    @Transactional
    public void journaliserEchec(
            Utilisateur utilisateur,
            TypeAction typeAction,
            String entiteConcernee,
            Long entiteId,
            String description) {

        journaliser(utilisateur, typeAction, entiteConcernee,
                entiteId, description, StatutAction.ECHEC);
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

        return journalActionRepository
                .search(utilisateurId, typeAction, statut, debut, fin, pageable)
                .map(journalActionMapper::toResponse); // ✅ map → DTO
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
}