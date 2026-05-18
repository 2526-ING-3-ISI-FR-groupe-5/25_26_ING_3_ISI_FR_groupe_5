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

    // ✅ AJOUTÉ — Retards par plage horaire
    @GetMapping("/plage/{plageHoraireId}/retards")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ASSISTANT', 'SUPER_ADMIN', 'ADMIN_INSTITUT')")
    public List<AppelsResponse> getRetardsByPlage(@PathVariable Long plageHoraireId) {
        return appelsMapper.toResponseList(appelsService.getRetardsByPlage(plageHoraireId));
    }

    // ✅ AJOUTÉ — Retards d'un étudiant
    @GetMapping("/etudiant/{etudiantId}/retards")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ASSISTANT', 'SUPER_ADMIN', 'ADMIN_INSTITUT', 'ETUDIANT')")
    public List<AppelsResponse> getRetardsByEtudiant(@PathVariable Long etudiantId) {
        return appelsMapper.toResponseList(appelsService.getRetardsByEtudiant(etudiantId));
    }

    // ══════════════════════════════════════════
    // POST — Création / Enregistrement
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

    /**
     * Validation autonome de présence par l'étudiant via QR code ou code PIN.
     *
     * ✅ CORRIGÉ — cast sécurisé : si l'utilisateur connecté n'est pas un Etudiant
     * (bug de configuration Spring Security), on retourne 403 au lieu d'un
     * ClassCastException qui produirait un 500 opaque.
     */
    @PostMapping("/valider-code")
    @PreAuthorize("hasRole('ETUDIANT')")
    public ResponseEntity<AppelsResponse> validerParCode(
            @RequestBody AppelsRequest req,
            @AuthenticationPrincipal Utilisateur u) {

        if (!(u instanceof Etudiant etudiant)) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(
                appelsMapper.toResponse(appelsService.validerParCode(req, etudiant.getId())));
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

    /**
     * ✅ AJOUTÉ — Ajustement du nombre d'heures de présence d'un appel.
     * Utilisé pour corriger manuellement une présence partielle.
     * Réservé aux enseignants et admins.
     */
    @PutMapping("/{id}/heures")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ASSISTANT', 'SUPER_ADMIN', 'ADMIN_INSTITUT')")
    public ResponseEntity<Void> ajusterHeures(
            @PathVariable Long id,
            @RequestParam int nbHeuresPresent) {
        appelsService.ajusterHeures(id, nbHeuresPresent);
        return ResponseEntity.noContent().build();
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