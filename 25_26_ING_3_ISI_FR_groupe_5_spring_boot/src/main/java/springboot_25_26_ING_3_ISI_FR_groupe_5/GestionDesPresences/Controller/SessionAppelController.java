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
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Mappers.SessionAppelMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Services.ServiceImple.SessionAppelService;

import java.util.List;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Classe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.SessionAppel;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Config.Security;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Etudiant;

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

    /**
     * GET /api/sessions-appel/{id}
     * Détail d'une session - Accessible Enseignant + Assistant
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ASSISTANT', 'SUPER_ADMIN', 'ADMIN_INSTITUT')")
    public SessionAppelResponse getById(@PathVariable Long id) {
        return sessionAppelMapper.toResponse(sessionAppelService.findById(id));
    }

    /**
     * GET /api/sessions-appel/plage/{plageHoraireId}
     * Historique des sessions d'une plage - Enseignant + Assistant
     */
    @GetMapping("/plage/{plageHoraireId}")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ASSISTANT', 'SUPER_ADMIN', 'ADMIN_INSTITUT')")
    public List<SessionAppelResponse> getByPlage(@PathVariable Long plageHoraireId) {
        return sessionAppelMapper.toResponseList(sessionAppelService.getByPlage(plageHoraireId));
    }

    /**
     * GET /api/sessions-appel/plage/{plageHoraireId}/active
     * ✅ Session active - ÉTUDIANT (pour valider) + Enseignant + Assistant (corrigé)
     */
    @GetMapping("/plage/{plageHoraireId}/active")
    @PreAuthorize("hasAnyRole('ETUDIANT', 'ENSEIGNANT', 'ASSISTANT', 'SUPER_ADMIN', 'ADMIN_INSTITUT')")
    public ResponseEntity<SessionAppelResponse> getSessionActive(@PathVariable Long plageHoraireId) {
        try {
            return ResponseEntity.ok(
                    sessionAppelMapper.toResponse(sessionAppelService.getSessionActive(plageHoraireId))
            );
        } catch (RuntimeException e) {
            return ResponseEntity.noContent().build();
        }
    }

    /**
     * GET /api/sessions-appel/classe/{classeId}/active
     * Session active pour une classe - ÉTUDIANT + Enseignant + Assistant
     */
    @GetMapping("/classe/{classeId}/active")
    @PreAuthorize("hasAnyRole('ETUDIANT', 'ENSEIGNANT', 'ASSISTANT', 'SUPER_ADMIN', 'ADMIN_INSTITUT')")
    public ResponseEntity<SessionAppelResponse> getSessionActivePourClasse(@PathVariable Long classeId) {
        var session = sessionAppelService.getSessionActivePourClasse(classeId);

        if (session == null) {
            return ResponseEntity.noContent().build();
        }

        SessionAppelResponse response = sessionAppelMapper.toResponse(session);

        // Si expirée, masquer le code
        if (session.isExpire()) {
            response.setCode(null);
        }

        return ResponseEntity.ok(response);
    }

    // ══════════════════════════════════════════
    // POST — Créer une session
    // ✅ ENSEIGNANT uniquement (corrigé)
    // ══════════════════════════════════════════

    @PostMapping
    @PreAuthorize("hasRole('ENSEIGNANT')")
    @ResponseStatus(HttpStatus.CREATED)
    public SessionAppelResponse creer(
            @Valid @RequestBody SessionAppelRequest req,
            @AuthenticationPrincipal Utilisateur enseignant) {

        log.info("ENSEIGNANT {} crée une session {} - plage: {}",
                enseignant.getId(), req.getMethode(), req.getPlageHoraireId());

        return sessionAppelMapper.toResponse(
                sessionAppelService.creer(req, enseignant.getId()));
    }

    // ══════════════════════════════════════════
    // PUT — Renouveler le code QR/PIN
    // ✅ ENSEIGNANT uniquement (corrigé)
    // ══════════════════════════════════════════

    @PutMapping("/{id}/renouveler")
    @PreAuthorize("hasRole('ENSEIGNANT')")
    public SessionAppelResponse renouvelerCode(
            @PathVariable Long id,
            @RequestParam(defaultValue = "3") @Min(1) @Max(60) int dureeMinutes) {

        log.info("ENSEIGNANT renouvelle le code - session: {}, durée: {}min", id, dureeMinutes);

        return sessionAppelMapper.toResponse(
                sessionAppelService.renouvelerCode(id, dureeMinutes));
    }

    // ══════════════════════════════════════════
    // PUT — Terminer le cours
    // ✅ ENSEIGNANT uniquement (corrigé)
    // ══════════════════════════════════════════

    @PutMapping("/{id}/terminer")
    @PreAuthorize("hasRole('ENSEIGNANT')")
    public SessionAppelResponse terminerCours(@PathVariable Long id) {
        log.info("ENSEIGNANT termine le cours - session: {}", id);
        return sessionAppelMapper.toResponse(sessionAppelService.terminerCours(id));
    }

    // ══════════════════════════════════════════
    // PUT — Arrêter la session (sans terminer le cours)
    // ✅ ENSEIGNANT uniquement
    // ══════════════════════════════════════════

    @PutMapping("/{id}/arreter")
    @PreAuthorize("hasRole('ENSEIGNANT')")
    public SessionAppelResponse arreterSession(@PathVariable Long id) {
        log.info("ENSEIGNANT arrête la session {}", id);
        var session = sessionAppelService.findById(id);
        session.setActif(false);
        return sessionAppelMapper.toResponse(sessionAppelService.findById(id));
    }

    // ══════════════════════════════════════════
    // DELETE — Admin uniquement
    // ══════════════════════════════════════════

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN_INSTITUT')")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        log.warn("Suppression session {}", id);
        return ResponseEntity.noContent().build();
    }
}