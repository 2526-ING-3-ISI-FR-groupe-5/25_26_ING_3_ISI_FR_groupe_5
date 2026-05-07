package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Mappers.AppelsMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Services.ServiceImple.AppelsService;

import java.util.List;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.appel.AppelRetardRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.appel.AppelsCheckManuelRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.appel.AppelsRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.appel.AppelsResponse;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.Appels;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Config.Security;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Etudiant;

@RestController
@RequestMapping("/api/appels")
@RequiredArgsConstructor
@Slf4j
public class AppelsController {

    private final AppelsService appelsService;
    private final AppelsMapper appelsMapper;

    // ══════════════════════════════════════════
    // GET — Consultation (Enseignant + Assistant)
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
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ASSISTANT', 'SUPER_ADMIN', 'ADMIN_INSTITUT')")
    public List<AppelsResponse> getByEtudiant(@PathVariable Long etudiantId) {
        return appelsMapper.toResponseList(appelsService.getByEtudiant(etudiantId));
    }

    @GetMapping("/session/{sessionId}")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ASSISTANT', 'SUPER_ADMIN', 'ADMIN_INSTITUT')")
    public List<AppelsResponse> getBySession(@PathVariable Long sessionId) {
        return appelsMapper.toResponseList(appelsService.getBySession(sessionId));
    }

    /**
     * Retards enregistrés sur une plage horaire.
     * Utile pour l'affichage dans le tableau de bord.
     */
    @GetMapping("/plage/{plageHoraireId}/retards")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ASSISTANT', 'SUPER_ADMIN', 'ADMIN_INSTITUT')")
    public List<AppelsResponse> getRetards(@PathVariable Long plageHoraireId) {
        return appelsMapper.toResponseList(appelsService.getRetardsByPlage(plageHoraireId));
    }

    // ══════════════════════════════════════════
    // POST — Création appel unitaire (Enseignant + Assistant)
    // ══════════════════════════════════════════

    @PostMapping
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ASSISTANT')")
    public AppelsResponse creer(@Valid @RequestBody AppelsRequest req) {
        log.info("Création appel unitaire - étudiant: {}", req.getEtudiantId());
        return appelsMapper.toResponse(appelsService.creer(req));
    }

    // ══════════════════════════════════════════
    // POST — Appel manuel en lot (check list)
    // Enseignant + Assistant peuvent marquer les présences
    // ══════════════════════════════════════════

    @PostMapping("/manuel")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ASSISTANT')")
    public List<AppelsResponse> enregistrerAppelManuel(
            @Valid @RequestBody AppelsCheckManuelRequest req) {
        log.info("Marquage manuel des présences - plage: {}", req.getPlageHoraireId());
        return appelsMapper.toResponseList(appelsService.enregistrerAppelManuel(req));
    }

    // ══════════════════════════════════════════
    // POST — Marquer un retard
    // ✅ Enseignant + Assistant (corrigé)
    // ══════════════════════════════════════════

    @PostMapping("/retard")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ASSISTANT')")
    public AppelsResponse marquerRetard(@Valid @RequestBody AppelRetardRequest req) {
        log.info("Marquage retard - étudiant: {}, heure: {}", req.getEtudiantId(), req.getHeureArrivee());
        return appelsMapper.toResponse(appelsService.marquerRetard(req));
    }

    // ══════════════════════════════════════════
    // POST — Validation par QR / PIN
    // ✅ ÉTUDIANT uniquement (corrigé)
    // ══════════════════════════════════════════

    @PostMapping("/valider-code")
    @PreAuthorize("hasRole('ETUDIANT')")
    public AppelsResponse validerParCode(@Valid @RequestBody AppelsRequest req) {
        log.info("ÉTUDIANT valide sa présence - session: {}", req.getSessionAppelId());
        return appelsMapper.toResponse(appelsService.validerParCode(req));
    }

    // ══════════════════════════════════════════
    // PUT — Modifier un appel (Enseignant + Assistant)
    // ══════════════════════════════════════════

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ASSISTANT')")
    public AppelsResponse modifier(@PathVariable Long id,
                                   @Valid @RequestBody AppelsRequest req) {
        log.info("Modification appel {}", id);
        return appelsMapper.toResponse(appelsService.modifier(id, req));
    }

    // ══════════════════════════════════════════
    // DELETE — Admin uniquement
    // ══════════════════════════════════════════

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN_INSTITUT')")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        log.warn("Suppression appel {}", id);
        appelsService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}