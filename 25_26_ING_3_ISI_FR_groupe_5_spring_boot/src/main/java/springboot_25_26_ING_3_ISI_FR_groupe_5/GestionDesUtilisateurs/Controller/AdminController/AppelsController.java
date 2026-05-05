package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Controller.AdminController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.appel.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers.AppelsMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.AppelsService;

import java.util.List;

@RestController
@RequestMapping("/api/appels")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ENSEIGNANT', 'SUPER_ADMIN', 'ADMIN_INSTITUT', 'ASSISTANT')")
public class AppelsController {

    private final AppelsService appelsService;
    private final AppelsMapper appelsMapper;

    // ══════════════════════════════════════════
    // GET
    // ══════════════════════════════════════════

    @GetMapping("/{id}")
    public AppelsResponse getById(@PathVariable Long id) {
        return appelsMapper.toResponse(appelsService.findById(id));
    }

    @GetMapping("/plage/{plageHoraireId}")
    public List<AppelsResponse> getByPlage(@PathVariable Long plageHoraireId) {
        return appelsMapper.toResponseList(appelsService.getByPlageHoraire(plageHoraireId));
    }

    @GetMapping("/etudiant/{etudiantId}")
    public List<AppelsResponse> getByEtudiant(@PathVariable Long etudiantId) {
        return appelsMapper.toResponseList(appelsService.getByEtudiant(etudiantId));
    }

    @GetMapping("/session/{sessionId}")
    public List<AppelsResponse> getBySession(@PathVariable Long sessionId) {
        return appelsMapper.toResponseList(appelsService.getBySession(sessionId));
    }

    /**
     * Retards enregistrés sur une plage horaire.
     * Utile pour l'affichage dans le tableau de bord enseignant.
     */
    @GetMapping("/plage/{plageHoraireId}/retards")
    public List<AppelsResponse> getRetards(@PathVariable Long plageHoraireId) {
        return appelsMapper.toResponseList(appelsService.getRetardsByPlage(plageHoraireId));
    }

    // ══════════════════════════════════════════
    // POST — Appel unitaire
    // ══════════════════════════════════════════

    @PostMapping
    public AppelsResponse creer(@Valid @RequestBody AppelsRequest req) {
        return appelsMapper.toResponse(appelsService.creer(req));
    }

    // ══════════════════════════════════════════
    // POST — Appel manuel en lot (check list)
    // ══════════════════════════════════════════

    @PostMapping("/manuel")
    public List<AppelsResponse> enregistrerAppelManuel(
            @Valid @RequestBody AppelsCheckManuelRequest req) {
        return appelsMapper.toResponseList(appelsService.enregistrerAppelManuel(req));
    }

    // ══════════════════════════════════════════
    // POST — Marquer un retard
    // Accessible uniquement sur le premier cours du matin.
    // ══════════════════════════════════════════

    @PostMapping("/retard")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'SUPER_ADMIN', 'ADMIN_INSTITUT')")
    public AppelsResponse marquerRetard(@Valid @RequestBody AppelRetardRequest req) {
        return appelsMapper.toResponse(appelsService.marquerRetard(req));
    }

    // ══════════════════════════════════════════
    // POST — Validation par QR / PIN (étudiant)
    // ══════════════════════════════════════════

    @PostMapping("/valider-code")
    @PreAuthorize("hasAnyRole('ETUDIANT', 'ENSEIGNANT', 'SUPER_ADMIN')")
    public AppelsResponse validerParCode(@Valid @RequestBody AppelsRequest req) {
        return appelsMapper.toResponse(appelsService.validerParCode(req));
    }

    // ══════════════════════════════════════════
    // PUT — Modifier un appel
    // ══════════════════════════════════════════

    @PutMapping("/{id}")
    public AppelsResponse modifier(@PathVariable Long id,
                                   @Valid @RequestBody AppelsRequest req) {
        return appelsMapper.toResponse(appelsService.modifier(id, req));
    }

    // ══════════════════════════════════════════
    // DELETE
    // ══════════════════════════════════════════

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN_INSTITUT')")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        appelsService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}