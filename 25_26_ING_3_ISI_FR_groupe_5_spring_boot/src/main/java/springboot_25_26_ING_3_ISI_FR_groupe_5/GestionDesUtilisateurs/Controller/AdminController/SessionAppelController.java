package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Controller.AdminController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.sessionAppel.SessionAppelRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.sessionAppel.SessionAppelResponse;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers.SessionAppelMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.SessionAppelService;

import java.util.List;

@RestController
@RequestMapping("/api/sessions-appel")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ENSEIGNANT', 'SUPER_ADMIN', 'ADMIN_INSTITUT')")
public class SessionAppelController {

    private final SessionAppelService sessionAppelService;
    private final SessionAppelMapper sessionAppelMapper;

    // ══════════════════════════════════════════
    // GET
    // ══════════════════════════════════════════

    @GetMapping("/{id}")
    public SessionAppelResponse getById(@PathVariable Long id) {
        return sessionAppelMapper.toResponse(sessionAppelService.findById(id));
    }

    @GetMapping("/plage/{plageHoraireId}")
    public List<SessionAppelResponse> getByPlage(@PathVariable Long plageHoraireId) {
        return sessionAppelMapper.toResponseList(sessionAppelService.getByPlage(plageHoraireId));
    }

    /**
     * Session actuellement active sur une plage.
     * Utilisée par le frontend pour afficher le QR/PIN en cours.
     */
    @GetMapping("/plage/{plageHoraireId}/active")
    public SessionAppelResponse getSessionActive(@PathVariable Long plageHoraireId) {
        return sessionAppelMapper.toResponse(sessionAppelService.getSessionActive(plageHoraireId));
    }

    // ══════════════════════════════════════════
    // POST — Ouvrir une session
    // L'enseignant connecté est automatiquement l'auteur.
    // ══════════════════════════════════════════

    @PostMapping
    public SessionAppelResponse creer(
            @Valid @RequestBody SessionAppelRequest req,
            @AuthenticationPrincipal Utilisateur enseignant) {
        return sessionAppelMapper.toResponse(
                sessionAppelService.creer(req, enseignant.getId()));
    }

    // ══════════════════════════════════════════
    // PUT — Renouveler le code QR/PIN
    // ══════════════════════════════════════════

    @PutMapping("/{id}/renouveler")
    public SessionAppelResponse renouvelerCode(
            @PathVariable Long id,
            @RequestParam(defaultValue = "3") int dureeMinutes) {
        return sessionAppelMapper.toResponse(
                sessionAppelService.renouvelerCode(id, dureeMinutes));
    }

    // ══════════════════════════════════════════
    // PUT — Terminer le cours
    // ══════════════════════════════════════════

    @PutMapping("/{id}/terminer")
    public SessionAppelResponse terminerCours(@PathVariable Long id) {
        return sessionAppelMapper.toResponse(sessionAppelService.terminerCours(id));
    }
}