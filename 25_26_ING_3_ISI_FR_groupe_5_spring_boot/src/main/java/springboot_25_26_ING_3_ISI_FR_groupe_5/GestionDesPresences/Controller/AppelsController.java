package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.appel.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Mappers.AppelsMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Services.ServiceImple.AppelsService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Etudiant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;

import java.util.List;

@RestController
@RequestMapping("/api/appels")
@RequiredArgsConstructor
public class AppelsController {

    private final AppelsService appelsService;
    private final AppelsMapper appelsMapper;

    // ══════════════════════════════════════════
    // GET — Consultation
    // ══════════════════════════════════════════

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ASSISTANT', 'SUPER_ADMIN', 'ADMIN_INSTITUT')")
    public AppelsResponse getById(@PathVariable Long id) {
        return appelsMapper.toResponse(appelsService.findById(id));
    }

    @GetMapping("/plage/{plageHoraireId}")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ASSISTANT', 'SUPER_ADMIN', 'ADMIN_INSTITUT')")
    public List<AppelsResponse> getByPlage(@PathVariable Long plageHoraireId) {
        return appelsMapper.toResponseList(appelsService.getByPlageHoraire(plageHoraireId));
    }

    @GetMapping("/etudiant/{etudiantId}")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ASSISTANT', 'SUPER_ADMIN', 'ADMIN_INSTITUT', 'ETUDIANT')")
    public List<AppelsResponse> getByEtudiant(@PathVariable Long etudiantId) {
        return appelsMapper.toResponseList(appelsService.getByEtudiant(etudiantId));
    }

    // ══════════════════════════════════════════
    // POST — Création/Modification
    // ══════════════════════════════════════════

    @PostMapping("/manuel")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ASSISTANT')")
    public List<AppelsResponse> enregistrerAppelManuel(
            @Valid @RequestBody AppelsCheckManuelRequest req,
            @AuthenticationPrincipal Utilisateur u) {
        if (u instanceof Enseignant e) {
            req.setEnseignantId(e.getId());
        }
        return appelsMapper.toResponseList(appelsService.enregistrerAppelManuel(req));
    }

    @PostMapping("/retard")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ASSISTANT')")
    public AppelsResponse marquerRetard(
            @Valid @RequestBody AppelRetardRequest req,
            @AuthenticationPrincipal Utilisateur u) {
        if (u instanceof Enseignant e) {
            req.setEnseignantId(e.getId());
        }
        return appelsMapper.toResponse(appelsService.marquerRetard(req));
    }

    @PostMapping("/valider-code")
    @PreAuthorize("hasRole('ETUDIANT')")
    public AppelsResponse validerParCode(
            @RequestBody AppelsRequest req,
            @AuthenticationPrincipal Utilisateur u) {
        return appelsMapper.toResponse(
                appelsService.validerParCode(req, ((Etudiant) u).getId()));
    }

    // ══════════════════════════════════════════
    // PUT — Mise à jour
    // ══════════════════════════════════════════

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ASSISTANT', 'SUPER_ADMIN', 'ADMIN_INSTITUT')")
    public AppelsResponse modifier(
            @PathVariable Long id,
            @Valid @RequestBody AppelsRequest req,
            @AuthenticationPrincipal Utilisateur u) {
        if (u instanceof Enseignant e) {
            req.setEnseignantId(e.getId());
        }
        return appelsMapper.toResponse(appelsService.modifier(id, req));
    }

    // ══════════════════════════════════════════
    // DELETE — Suppression
    // ══════════════════════════════════════════

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN_INSTITUT')")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        appelsService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}