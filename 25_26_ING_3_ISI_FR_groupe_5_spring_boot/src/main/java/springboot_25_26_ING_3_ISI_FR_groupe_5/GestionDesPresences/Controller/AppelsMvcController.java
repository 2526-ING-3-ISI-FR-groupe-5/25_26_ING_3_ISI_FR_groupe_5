package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.PlageHoraire.PlageHoraireResponse;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.appel.AppelsCheckManuelRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.appel.AppelRetardRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.sessionAppel.SessionAppelRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Enum.MethodeValidation;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Enum.StatutPresence;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Repository.AppelsRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Services.ServiceImple.AppelsService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Services.ServiceImple.PlageHoraireService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Services.ServiceImple.SessionAppelService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;

import java.time.LocalTime;
import java.util.List;

/**
 * Controller MVC pour les vues Thymeleaf du module appel.
 *
 * ✅ CORRIGÉ — Ce controller remplace les routes dupliquées qui existaient
 * dans EspaceEnseignantController. Les routes /enseignant/appels/** sont
 * gérées ici uniquement pour éviter AmbiguousHandlerMappingException.
 *
 * EspaceEnseignantController ne doit PAS déclarer ces mêmes routes.
 */
@Slf4j
@Controller
@RequestMapping("/enseignant/appels")
@RequiredArgsConstructor
public class AppelsMvcController {

    private final AppelsService appelsService;
    private final PlageHoraireService plageHoraireService;
    private final SessionAppelService sessionAppelService;
    private final AppelsRepository appelsRepository;

    // ══════════════════════════════════════════
    // AFFICHAGE PAGE D'APPEL
    // ══════════════════════════════════════════

    /**
     * GET /enseignant/appels/{id}/appel
     * Affiche la page d'appel pour une plage horaire.
     */
    @GetMapping("/{id}/appel")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ASSISTANT')")
    public String afficherAppel(
            @PathVariable Long id,
            Model model,
            @AuthenticationPrincipal Utilisateur user) {

        PlageHoraireResponse plage = plageHoraireService.findById(id);
        model.addAttribute("plageHoraire", plage);

        var appels = appelsService.getByPlageHoraire(id);
        model.addAttribute("appels", appels);

        model.addAttribute("nbPresents",
                appelsRepository.findByPlageHoraireIdAndStatut(id, StatutPresence.PRESENT).size());
        model.addAttribute("nbRetards",
                appelsRepository.findByPlageHoraireIdAndStatut(id, StatutPresence.RETARD).size());
        model.addAttribute("nbAbsents",
                appelsRepository.findByPlageHoraireIdAndStatut(id, StatutPresence.ABSENT).size());

        // ✅ CORRIGÉ — getSessionActive() lançait une exception si pas de session active
        // → null-safe avec try/catch
        try {
            model.addAttribute("sessionActive", sessionAppelService.getSessionActive(id));
        } catch (Exception e) {
            log.debug("Aucune session active pour la plage {} : {}", id, e.getMessage());
            model.addAttribute("sessionActive", null);
        }

        model.addAttribute("sessions", sessionAppelService.getByPlage(id));

        // ✅ CORRIGÉ — règle métier : premier cours du matin = heureDebut <= 08h30
        // AVANT : heureDebut.hour < 9 (acceptait 08h59)
        LocalTime heureDebut = plage.getHeureDebut();
        boolean estPremierCoursMatin = heureDebut != null
                && (heureDebut.getHour() < 8
                || (heureDebut.getHour() == 8 && heureDebut.getMinute() <= 30));
        model.addAttribute("estPremierCoursMatin", estPremierCoursMatin);

        return "enseignant/appel_interface";
    }

    // ══════════════════════════════════════════
    // CHECK MANUEL DES PRÉSENCES
    // ══════════════════════════════════════════

    @PostMapping("/{id}/check-manuel")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ASSISTANT')")
    public String checkManuel(
            @PathVariable Long id,
            @RequestParam(required = false) List<Long> etudiantIdsPresents,
            @AuthenticationPrincipal Utilisateur user,
            RedirectAttributes ra) {
        try {
            AppelsCheckManuelRequest req = new AppelsCheckManuelRequest();
            req.setPlageHoraireId(id);
            req.setEtudiantIdsPresents(etudiantIdsPresents != null ? etudiantIdsPresents : List.of());
            if (user instanceof Enseignant e) req.setEnseignantId(e.getId());
            appelsService.enregistrerAppelManuel(req);
            ra.addFlashAttribute("succes", "Presences enregistrees avec succes !");
        } catch (Exception e) {
            log.error("Erreur check manuel plage {}", id, e);
            ra.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/enseignant/appels/" + id + "/appel";
    }

    // ══════════════════════════════════════════
    // RETARD
    // ══════════════════════════════════════════

    @PostMapping("/{id}/marquer-retard")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ASSISTANT')")
    public String marquerRetard(
            @PathVariable Long id,
            @RequestParam Long etudiantId,
            @RequestParam LocalTime heureArrivee,
            @AuthenticationPrincipal Utilisateur user,
            RedirectAttributes ra) {
        try {
            AppelRetardRequest req = new AppelRetardRequest();
            req.setEtudiantId(etudiantId);
            req.setPlageHoraireId(id);
            req.setHeureArrivee(heureArrivee);
            if (user instanceof Enseignant e) req.setEnseignantId(e.getId());
            appelsService.marquerRetard(req);
            ra.addFlashAttribute("succes", "Retard enregistre !");
        } catch (Exception e) {
            log.error("Erreur retard plage {} etudiant {}", id, etudiantId, e);
            ra.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/enseignant/appels/" + id + "/appel";
    }

    // ══════════════════════════════════════════
    // SESSION QR / PIN
    // ══════════════════════════════════════════

    @PostMapping("/{id}/lancer-session")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ASSISTANT')")
    public String lancerSession(
            @PathVariable Long id,
            @RequestParam String methode,
            @RequestParam(defaultValue = "3") int dureeMinutes,
            @AuthenticationPrincipal Utilisateur user,
            RedirectAttributes ra) {
        try {
            SessionAppelRequest req = new SessionAppelRequest();
            req.setPlageHoraireId(id);
            req.setMethode(MethodeValidation.valueOf(methode));
            req.setDureeMinutes(dureeMinutes);
            sessionAppelService.creer(req, user.getId());
            ra.addFlashAttribute("succes", "Session " + methode + " lancee !");
        } catch (Exception e) {
            log.error("Erreur lancement session plage {}", id, e);
            ra.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/enseignant/appels/" + id + "/appel";
    }

    @PostMapping("/{id}/arreter-session/{sid}")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ASSISTANT')")
    public String arreterSession(
            @PathVariable Long id,
            @PathVariable Long sid,
            RedirectAttributes ra) {
        try {
            sessionAppelService.arreterSession(sid);
            ra.addFlashAttribute("succes", "Session arretee.");
        } catch (Exception e) {
            log.error("Erreur arret session {}", sid, e);
            ra.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/enseignant/appels/" + id + "/appel";
    }

    @PostMapping("/{id}/terminer/{sid}")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ASSISTANT')")
    public String terminerCours(
            @PathVariable Long id,
            @PathVariable Long sid,
            RedirectAttributes ra) {
        try {
            sessionAppelService.terminerCours(sid);
            ra.addFlashAttribute("succes", "Cours termine. Les absences ont ete enregistrees.");
        } catch (Exception e) {
            log.error("Erreur terminaison cours session {}", sid, e);
            ra.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/enseignant/appels/" + id + "/appel";
    }

    // ══════════════════════════════════════════
    // AJUSTEMENT HEURES
    // ══════════════════════════════════════════

    @PostMapping("/{id}/ajuster-heures/{aid}")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ASSISTANT')")
    public String ajusterHeures(
            @PathVariable Long id,
            @PathVariable Long aid,
            @RequestParam int nbHeuresPresent,
            RedirectAttributes ra) {
        try {
            appelsService.ajusterHeures(aid, nbHeuresPresent);
            ra.addFlashAttribute("succes", "Heures ajustees.");
        } catch (Exception e) {
            log.error("Erreur ajustement heures appel {}", aid, e);
            ra.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/enseignant/appels/" + id + "/appel";
    }
}