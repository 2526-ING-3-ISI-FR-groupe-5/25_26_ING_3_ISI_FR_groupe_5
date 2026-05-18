package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.appel.AppelRetardRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.appel.AppelsCheckManuelRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.sessionAppel.SessionAppelRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.PlageHoraire;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Enum.MethodeValidation;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Mappers.AppelsMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Services.ServiceImple.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/enseignant")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ENSEIGNANT')")
public class EspaceEnseignantController {

    private final StatsService statsService;
    private final PlageHoraireService plageHoraireService;
    private final AppelsService appelsService;
    private final SessionAppelService sessionAppelService;
    private final AppelsMapper appelsMapper;

    // ══════════════════════════════════════════
    // DASHBOARD PRINCIPAL
    // ══════════════════════════════════════════

    @GetMapping("/mon-espace")
    public String dashboard(Model model, @AuthenticationPrincipal Utilisateur u) {
        if (!(u instanceof Enseignant enseignant)) {
            return "redirect:/accessDenied";
        }

        Long ensId = enseignant.getId();

        var uesProgression = statsService.getProgressionEnseignant(ensId);
        var coursAujourdhui = plageHoraireService.findCoursEnseignantAujourdhui(ensId, LocalDate.now());

        // ✅ AJOUTÉ — tauxMoyen calculé côté Java
        // Évite le risque NullPointerException du SpEL uesProgression.![progression].average()
        int tauxMoyen = (uesProgression == null || uesProgression.isEmpty()) ? 0
                : (int) uesProgression.stream()
                .mapToInt(s -> (int) s.getProgression()) // Plus besoin de check du null si c'est un double primitif
                .average()
                .orElse(0);

        model.addAttribute("uesProgression", uesProgression);
        model.addAttribute("coursAujourdhui", coursAujourdhui);
        model.addAttribute("tauxMoyen", tauxMoyen);  // ✅ AJOUTÉ

        return "enseignant/dashboardEnseignant";
    }

    // ══════════════════════════════════════════
    // INTERFACE D'APPEL
    // ══════════════════════════════════════════

    @GetMapping("/appels/cours/{plageId}")
    public String interfaceAppel(@PathVariable Long plageId, Model model) {
        PlageHoraire ph = plageHoraireService.findEntityById(plageId);
        var appels = appelsService.getByPlageHoraire(plageId);

        model.addAttribute("plageHoraire", ph);
        model.addAttribute("appels", appelsMapper.toResponseList(appels));

        // Session active — null-safe
        try {
            model.addAttribute("sessionActive", sessionAppelService.getSessionActive(plageId));
        } catch (Exception e) {
            model.addAttribute("sessionActive", null);
        }

        // ✅ AJOUTÉ — historique sessions pour affichage en bas de page
        model.addAttribute("sessions",
                sessionAppelService.getByPlage(plageId));

        // Compteurs
        model.addAttribute("nbPresents", appels.stream().filter(a -> a.isPresent()).count());
        model.addAttribute("nbRetards",  appels.stream().filter(a -> a.isRetard()).count());
        model.addAttribute("nbAbsents",  appels.stream().filter(a -> a.isAbsentToutLeCours()).count());

        return "appels/appel_interface";
    }

    // ══════════════════════════════════════════
    // LANCER UNE SESSION (QR / PIN)
    // ══════════════════════════════════════════

    /**
     * ✅ AJOUTÉ — Route manquante pour les boutons "Générer QR" et "Générer PIN"
     * du template appel_interface.html
     * POST /enseignant/appels/{plageId}/lancer-session
     */
    @PostMapping("/appels/{plageId}/lancer-session")
    public String lancerSession(
            @PathVariable Long plageId,
            @RequestParam MethodeValidation methode,
            @RequestParam(defaultValue = "3") int dureeMinutes,
            RedirectAttributes ra,
            @AuthenticationPrincipal Utilisateur u) {

        if (!(u instanceof Enseignant enseignant)) {
            return "redirect:/accessDenied";
        }

        try {
            SessionAppelRequest req = SessionAppelRequest.builder()
                    .plageHoraireId(plageId)
                    .methode(methode)
                    .dureeMinutes(dureeMinutes)
                    .build();
            sessionAppelService.creer(req, enseignant.getId());
            ra.addFlashAttribute("succes", "Session " + methode.name() + " lancee avec succes.");
        } catch (Exception e) {
            log.error("Erreur lancement session plage {}", plageId, e);
            ra.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/enseignant/appels/cours/" + plageId;
    }

    // ══════════════════════════════════════════
    // ARRÊTER / TERMINER UNE SESSION
    // ══════════════════════════════════════════

    /**
     * ✅ AJOUTÉ — Route manquante pour le bouton "Arrêter"
     * POST /enseignant/appels/{plageId}/arreter-session/{sessionId}
     */
    @PostMapping("/appels/{plageId}/arreter-session/{sessionId}")
    public String arreterSession(
            @PathVariable Long plageId,
            @PathVariable Long sessionId,
            RedirectAttributes ra) {
        try {
            sessionAppelService.arreterSession(sessionId);
            ra.addFlashAttribute("succes", "Session arretee.");
        } catch (Exception e) {
            log.error("Erreur arret session {}", sessionId, e);
            ra.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/enseignant/appels/cours/" + plageId;
    }

    /**
     * ✅ AJOUTÉ — Route manquante pour le bouton "Terminer le cours"
     * POST /enseignant/appels/{plageId}/terminer/{sessionId}
     */
    @PostMapping("/appels/{plageId}/terminer/{sessionId}")
    public String terminerCours(
            @PathVariable Long plageId,
            @PathVariable Long sessionId,
            RedirectAttributes ra) {
        try {
            sessionAppelService.terminerCours(sessionId);
            ra.addFlashAttribute("succes", "Cours termine. Les absences ont ete enregistrees.");
        } catch (Exception e) {
            log.error("Erreur terminaison cours session {}", sessionId, e);
            ra.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/enseignant/appels/cours/" + plageId;
    }

    // ══════════════════════════════════════════
    // CHECK MANUEL DES PRÉSENCES
    // ══════════════════════════════════════════

    /**
     * ✅ AJOUTÉ — Route manquante pour le formulaire "Enregistrer les présences"
     * POST /enseignant/appels/{plageId}/check-manuel
     */
    @PostMapping("/appels/{plageId}/check-manuel")
    public String checkManuel(
            @PathVariable Long plageId,
            @ModelAttribute AppelsCheckManuelRequest req,
            RedirectAttributes ra,
            @AuthenticationPrincipal Utilisateur u) {

        if (!(u instanceof Enseignant enseignant)) {
            return "redirect:/accessDenied";
        }

        try {
            req.setPlageHoraireId(plageId);
            req.setEnseignantId(enseignant.getId());
            appelsService.enregistrerAppelManuel(req);
            ra.addFlashAttribute("succes", "Presences enregistrees avec succes.");
        } catch (Exception e) {
            log.error("Erreur check manuel plage {}", plageId, e);
            ra.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/enseignant/appels/cours/" + plageId;
    }

    // ══════════════════════════════════════════
    // MARQUER UN RETARD
    // ══════════════════════════════════════════

    /**
     * ✅ AJOUTÉ — Route manquante pour le formulaire "Retard"
     * POST /enseignant/appels/{plageId}/marquer-retard
     */
    @PostMapping("/appels/{plageId}/marquer-retard")
    public String marquerRetard(
            @PathVariable Long plageId,
            @RequestParam Long etudiantId,
            @RequestParam LocalTime heureArrivee,
            @RequestParam(required = false) String commentaire,
            RedirectAttributes ra,
            @AuthenticationPrincipal Utilisateur u) {

        if (!(u instanceof Enseignant enseignant)) {
            return "redirect:/accessDenied";
        }

        try {
            AppelRetardRequest req = AppelRetardRequest.builder()
                    .plageHoraireId(plageId)
                    .etudiantId(etudiantId)
                    .enseignantId(enseignant.getId())
                    .heureArrivee(heureArrivee)
                    .commentaire(commentaire)
                    .build();
            appelsService.marquerRetard(req);
            ra.addFlashAttribute("succes", "Retard enregistre.");
        } catch (Exception e) {
            log.error("Erreur retard plage {} etudiant {}", plageId, etudiantId, e);
            ra.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/enseignant/appels/cours/" + plageId;
    }

    // ══════════════════════════════════════════
    // AJUSTER LES HEURES DE PRÉSENCE
    // ══════════════════════════════════════════

    /**
     * ✅ AJOUTÉ — Route manquante pour les boutons +/- d'heures
     * POST /enseignant/appels/{plageId}/ajuster-heures/{appelId}
     */
    @PostMapping("/appels/{plageId}/ajuster-heures/{appelId}")
    public String ajusterHeures(
            @PathVariable Long plageId,
            @PathVariable Long appelId,
            @RequestParam int nbHeuresPresent,
            RedirectAttributes ra) {
        try {
            appelsService.ajusterHeures(appelId, nbHeuresPresent);
            ra.addFlashAttribute("succes", "Heures ajustees.");
        } catch (Exception e) {
            log.error("Erreur ajustement heures appel {}", appelId, e);
            ra.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/enseignant/appels/cours/" + plageId;
    }
}