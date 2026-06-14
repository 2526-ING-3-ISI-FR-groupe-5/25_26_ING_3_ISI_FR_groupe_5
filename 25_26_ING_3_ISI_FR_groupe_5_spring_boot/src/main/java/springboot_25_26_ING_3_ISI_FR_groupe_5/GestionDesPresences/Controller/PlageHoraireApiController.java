package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.PlageHoraire.PlageHoraireRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.PlageHoraire.PlageHoraireResponse;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.PlageHoraire;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Services.InterfaceService.IPlageHoraireService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur REST exposant les plages horaires pour le calendrier
 * (FullCalendar) consommé par classe.html.
 *
 * Endpoints attendus par le JS :
 *  - GET    /api/plages?classeId=...&dateDebut=...
 *  - POST   /api/plages
 *  - PUT    /api/plages/{id}
 *  - DELETE /api/plages/{id}
 */
@Slf4j
@RestController
@RequestMapping("/api/plages")
@RequiredArgsConstructor
public class PlageHoraireApiController {

    private final IPlageHoraireService plageHoraireService;

    // ============================================
    // GET /api/plages?classeId=...&dateDebut=...
    // ============================================
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN_INSTITUT', 'ASSISTANT', 'ENSEIGNANT', 'ETUDIANT')")
    public List<PlageHoraireResponse> getPlages(
            @RequestParam Long classeId,
            @RequestParam(required = false) LocalDate dateDebut) {

        if (dateDebut == null) {
            dateDebut = LocalDate.now();
        }
        LocalDate dateFin = dateDebut.plusDays(6);

        return plageHoraireService.getByClasseAndSemaine(classeId, dateDebut, dateFin);
    }

    // ============================================
    // POST /api/plages
    // ============================================
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN_INSTITUT') or (hasRole('ASSISTANT') and @institutSecurityService.peutGererClasse(#request.classeId))")
    public ResponseEntity<?> creerPlage(
            @RequestBody PlageHoraireRequest request,
            @AuthenticationPrincipal Utilisateur auteur) {

        try {
            PlageHoraireResponse plage = plageHoraireService.creer(request, auteur);
            return ResponseEntity.status(HttpStatus.CREATED).body(plage);
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.warn("Création plage refusée (API) : {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            log.error("Erreur création plage (API)", e);
            return ResponseEntity.internalServerError().body(Map.of("message", "Erreur serveur"));
        }
    }

    // ============================================
    // PUT /api/plages/{id}
    // ============================================
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN_INSTITUT', 'ASSISTANT')")
    public ResponseEntity<?> modifierPlage(
            @PathVariable Long id,
            @RequestBody PlageHoraireRequest request,
            @AuthenticationPrincipal Utilisateur auteur) {

        try {
            // Le JS envoie parfois un payload partiel (déplacement / redimensionnement).
            // On complète les champs manquants avec les valeurs actuelles de la plage.
            PlageHoraire existante = plageHoraireService.findEntityById(id);

            if (request.getClasseId() == null && existante.getClasse() != null) {
                request.setClasseId(existante.getClasse().getId());
            }
            if (request.getJour() == null) {
                request.setJour(existante.getJour());
            }
            if (request.getHeureDebut() == null) {
                request.setHeureDebut(existante.getHeureDebut());
            }
            if (request.getHeureFin() == null) {
                request.setHeureFin(existante.getHeureFin());
            }
            if (request.getSalle() == null) {
                request.setSalle(existante.getSalle());
            }
            if (request.getCouleur() == null) {
                request.setCouleur(existante.getCouleur());
            }
            if (request.getTitre() == null) {
                request.setTitre(existante.getTitre());
            }
            if (request.getTypeSeance() == null) {
                request.setTypeSeance(existante.getTypeSeance());
            }
            if (request.getProgrammationUEId() == null && existante.getProgrammationUE() != null) {
                request.setProgrammationUEId(existante.getProgrammationUE().getId());
            }

            // Garde-fou : si heureFin <= heureDebut (ex : end non défini côté client),
            // on retombe sur les heures existantes pour éviter un rejet inattendu.
            if (request.getHeureDebut() != null && request.getHeureFin() != null
                    && !request.getHeureDebut().isBefore(request.getHeureFin())) {
                request.setHeureDebut(existante.getHeureDebut());
                request.setHeureFin(existante.getHeureFin());
            }

            PlageHoraireResponse plage = plageHoraireService.modifier(id, request, auteur);
            return ResponseEntity.ok(plage);
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.warn("Modification plage refusée (API) : {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            log.error("Erreur modification plage (API)", e);
            return ResponseEntity.internalServerError().body(Map.of("message", "Erreur serveur"));
        }
    }

    // ============================================
    // DELETE /api/plages/{id}
    // ============================================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN_INSTITUT', 'ASSISTANT')")
    public ResponseEntity<?> supprimerPlage(
            @PathVariable Long id,
            @AuthenticationPrincipal Utilisateur auteur) {

        try {
            plageHoraireService.supprimer(id, auteur);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Erreur suppression plage (API)", e);
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
