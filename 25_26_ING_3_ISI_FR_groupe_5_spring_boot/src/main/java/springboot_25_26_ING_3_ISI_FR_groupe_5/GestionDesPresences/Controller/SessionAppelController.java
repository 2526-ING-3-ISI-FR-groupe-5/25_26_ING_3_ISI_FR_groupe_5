package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.sessionAppel.SessionAppelRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.sessionAppel.SessionAppelResponse;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Mappers.SessionAppelMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Services.ServiceImple.SessionAppelService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Etudiant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exception.ResourceNotFoundException;

import java.util.List;

@RestController
@RequestMapping("/api/sessions-appel")
@RequiredArgsConstructor
@Slf4j
public class SessionAppelController {

    private final SessionAppelService sessionAppelService;
    private final SessionAppelMapper sessionAppelMapper;

    // ══════════════════════════════════════════
    // GET — Consultation
    // ══════════════════════════════════════════

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ASSISTANT', 'SUPER_ADMIN', 'ADMIN_INSTITUT')")
    public SessionAppelResponse getById(@PathVariable Long id) {
        return sessionAppelMapper.toResponse(sessionAppelService.findById(id));
    }

    @GetMapping("/plage/{plageHoraireId}")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ASSISTANT', 'SUPER_ADMIN', 'ADMIN_INSTITUT')")
    public List<SessionAppelResponse> getByPlage(@PathVariable Long plageHoraireId) {
        return sessionAppelMapper.toResponseList(sessionAppelService.getByPlage(plageHoraireId));
    }

    @GetMapping("/plage/{plageHoraireId}/active")
    @PreAuthorize("hasAnyRole('ETUDIANT', 'ENSEIGNANT', 'ASSISTANT', 'SUPER_ADMIN', 'ADMIN_INSTITUT')")
    public ResponseEntity<SessionAppelResponse> getSessionActive(
            @PathVariable Long plageHoraireId,
            @AuthenticationPrincipal Utilisateur utilisateur) {
        try {
            var session = sessionAppelService.getSessionActive(plageHoraireId);

            // Sécurité : un étudiant ne peut consulter que les sessions de sa classe
            if (utilisateur instanceof Etudiant etudiant) {
                var classeSession = session.getPlageHoraire() != null
                        ? session.getPlageHoraire().getClasse() : null;
                if (classeSession == null
                        || etudiant.getClasse() == null
                        || !classeSession.getId().equals(etudiant.getClasse().getId())) {
                    return ResponseEntity.status(403).build();
                }
            }

            var response = sessionAppelMapper.toResponse(session);
            masquerCodePourEtudiant(response, utilisateur);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            // Cas normal : aucune session active pour cette plage
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/classe/{classeId}/active")
    @PreAuthorize("hasAnyRole('ETUDIANT', 'ENSEIGNANT', 'ASSISTANT', 'SUPER_ADMIN', 'ADMIN_INSTITUT')")
    public ResponseEntity<SessionAppelResponse> getSessionActivePourClasse(
            @PathVariable Long classeId,
            @AuthenticationPrincipal Utilisateur utilisateur) {

        // Sécurité : un étudiant ne peut consulter que sa propre classe
        if (utilisateur instanceof Etudiant etudiant) {
            if (etudiant.getClasse() == null
                    || !classeId.equals(etudiant.getClasse().getId())) {
                return ResponseEntity.status(403).build();
            }
        }

        var session = sessionAppelService.getSessionActivePourClasse(classeId);
        if (session == null) return ResponseEntity.noContent().build();

        SessionAppelResponse response = sessionAppelMapper.toResponse(session);
        if (session.isExpire()) response.setCode(null);
        masquerCodePourEtudiant(response, utilisateur);

        return ResponseEntity.ok(response);
    }

    // ══════════════════════════════════════════
    // POST — Créer une session
    // ══════════════════════════════════════════

    @PostMapping
    @PreAuthorize("hasRole('ENSEIGNANT')")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<SessionAppelResponse> creer(
            @Valid @RequestBody SessionAppelRequest req,
            @AuthenticationPrincipal Utilisateur utilisateur) {

        if (!(utilisateur instanceof Enseignant enseignant)) {
            return ResponseEntity.status(403).build();
        }

        log.info("ENSEIGNANT {} crée une session {} - plage: {}",
                enseignant.getId(), req.getMethode(), req.getPlageHoraireId());

        return ResponseEntity.status(HttpStatus.CREATED).body(
                sessionAppelMapper.toResponse(
                        sessionAppelService.creer(req, enseignant.getId())));
    }

    // ══════════════════════════════════════════
    // PUT — Actions sur une session (Sécurisées)
    // ══════════════════════════════════════════

    @PutMapping("/{id}/renouveler")
    @PreAuthorize("hasRole('ENSEIGNANT')")
    public ResponseEntity<SessionAppelResponse> renouvelerCode(
            @PathVariable Long id,
            @RequestParam(defaultValue = "3") @Min(1) @Max(60) int dureeMinutes,
            @AuthenticationPrincipal Utilisateur utilisateur) {

        if (!(utilisateur instanceof Enseignant enseignant)) {
            return ResponseEntity.status(403).build();
        }

        log.info("ENSEIGNANT {} renouvelle le code - session: {}, durée: {}min",
                enseignant.getId(), id, dureeMinutes);

        return ResponseEntity.ok(
                sessionAppelMapper.toResponse(
                        sessionAppelService.renouvelerCode(id, dureeMinutes, enseignant.getId())));
    }

    /**
     * ✅ CORRIGÉ — Cast sécurisé et transmission du enseignant.getId()
     */
    @PutMapping("/{id}/terminer")
    @PreAuthorize("hasRole('ENSEIGNANT')")
    public ResponseEntity<SessionAppelResponse> terminerCours(
            @PathVariable Long id,
            @AuthenticationPrincipal Utilisateur utilisateur) {

        if (!(utilisateur instanceof Enseignant enseignant)) {
            return ResponseEntity.status(403).build();
        }

        log.info("ENSEIGNANT {} termine le cours - session: {}", enseignant.getId(), id);
        return ResponseEntity.ok(
                sessionAppelMapper.toResponse(
                        sessionAppelService.terminerCours(id, enseignant.getId())));
    }

    /**
     * ✅ CORRIGÉ — Cast sécurisé et transmission du enseignant.getId()
     */
    @PutMapping("/{id}/arreter")
    @PreAuthorize("hasRole('ENSEIGNANT')")
    public ResponseEntity<SessionAppelResponse> arreterSession(
            @PathVariable Long id,
            @AuthenticationPrincipal Utilisateur utilisateur) {

        if (!(utilisateur instanceof Enseignant enseignant)) {
            return ResponseEntity.status(403).build();
        }

        log.info("ENSEIGNANT {} arrête la session {}", enseignant.getId(), id);
        return ResponseEntity.ok(
                sessionAppelMapper.toResponse(
                        sessionAppelService.arreterSession(id, enseignant.getId())));
    }

    // ══════════════════════════════════════════
    // DELETE
    // ══════════════════════════════════════════

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN_INSTITUT')")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        log.warn("ADMIN supprime la session {}", id);
        sessionAppelService.supprimer(id);
        return ResponseEntity.noContent().build();
    }

    // ══════════════════════════════════════════
    // PRIVÉ
    // ══════════════════════════════════════════

    private void masquerCodePourEtudiant(SessionAppelResponse response, Utilisateur utilisateur) {
        if (utilisateur instanceof Etudiant) {
            response.setCode(null);
        }
    }
}