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
            var response = sessionAppelMapper.toResponse(
                    sessionAppelService.getSessionActive(plageHoraireId));
            masquerCodePourEtudiant(response, utilisateur);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/classe/{classeId}/active")
    @PreAuthorize("hasAnyRole('ETUDIANT', 'ENSEIGNANT', 'ASSISTANT', 'SUPER_ADMIN', 'ADMIN_INSTITUT')")
    public ResponseEntity<SessionAppelResponse> getSessionActivePourClasse(
            @PathVariable Long classeId,
            @AuthenticationPrincipal Utilisateur utilisateur) {

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

    /**
     * ✅ CORRIGÉ — Cast sécurisé sur l'enseignant connecté.
     * Avant : enseignant.getId() appelé sur un Utilisateur générique
     * → silencieusement incorrect si ce n'est pas un Enseignant.
     * Après : vérification instanceof + 403 propre.
     */
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
    // PUT — Actions sur une session
    // ══════════════════════════════════════════

    /**
     * ✅ CORRIGÉ — On passe l'enseignantId au service pour vérifier
     * que seul l'enseignant propriétaire peut renouveler son code.
     * Avant : n'importe quel enseignant pouvait renouveler n'importe quelle session.
     */
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

    @PutMapping("/{id}/terminer")
    @PreAuthorize("hasRole('ENSEIGNANT')")
    public SessionAppelResponse terminerCours(@PathVariable Long id) {
        log.info("ENSEIGNANT termine le cours - session: {}", id);
        return sessionAppelMapper.toResponse(sessionAppelService.terminerCours(id));
    }

    @PutMapping("/{id}/arreter")
    @PreAuthorize("hasRole('ENSEIGNANT')")
    public SessionAppelResponse arreterSession(@PathVariable Long id) {
        log.info("ENSEIGNANT arrête la session {}", id);
        return sessionAppelMapper.toResponse(sessionAppelService.arreterSession(id));
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