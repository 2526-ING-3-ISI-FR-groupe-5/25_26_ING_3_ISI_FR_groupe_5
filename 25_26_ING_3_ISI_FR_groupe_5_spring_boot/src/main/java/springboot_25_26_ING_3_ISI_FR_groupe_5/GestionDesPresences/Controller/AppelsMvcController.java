package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Controller;

import lombok.RequiredArgsConstructor;
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
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.SessionAppel;
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

@Controller
@RequestMapping("/enseignant/appels")
@RequiredArgsConstructor
public class AppelsMvcController {

    private final AppelsService appelsService;
    private final PlageHoraireService plageHoraireService;
    private final SessionAppelService sessionAppelService;
    private final AppelsRepository appelsRepository;

    /**
     * GET /enseignant/appels/{id}/appel
     * Affiche la page d'appel pour une plage horaire
     */
    @GetMapping("/{id}/appel")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ASSISTANT')")
    public String afficherAppel(@PathVariable Long id,
                                Model model,
                                @AuthenticationPrincipal Utilisateur user) {

        PlageHoraireResponse plage = plageHoraireService.findById(id);
        model.addAttribute("plageHoraire", plage);

        var appels = appelsService.getByPlageHoraire(id);
        model.addAttribute("appels", appels);

        model.addAttribute("nbPresents", appelsRepository.findByPlageHoraireIdAndStatut(id, StatutPresence.PRESENT).size());
        model.addAttribute("nbRetards", appelsRepository.findByPlageHoraireIdAndStatut(id, StatutPresence.RETARD).size());
        model.addAttribute("nbAbsents", appelsRepository.findByPlageHoraireIdAndStatut(id, StatutPresence.ABSENT).size());

        SessionAppel sessionActive = sessionAppelService.getSessionActive(id);
        model.addAttribute("sessionActive", sessionActive);

        var sessions = sessionAppelService.getByPlage(id);
        model.addAttribute("sessions", sessions);

        boolean estPremierCoursMatin = plage.getHeureDebut().getHour() < 9;
        model.addAttribute("estPremierCoursMatin", estPremierCoursMatin);

        return "enseignant/appel_interface";  // ✅ Correspond à templates/enseignant/appel_interface.html
    }

    /**
     * POST /enseignant/appels/{id}/check-manuel
     * Enregistre les présences manuelles
     */
    @PostMapping("/{id}/check-manuel")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ASSISTANT')")
    public String checkManuel(@PathVariable Long id,  // ✅ id = plageHoraireId
                              @RequestParam(required = false) List<Long> etudiantIdsPresents,
                              @AuthenticationPrincipal Utilisateur user,
                              RedirectAttributes redirectAttributes) {
        try {
            AppelsCheckManuelRequest req = new AppelsCheckManuelRequest();
            req.setPlageHoraireId(id);  // ✅ Utilise le path variable
            req.setEtudiantIdsPresents(etudiantIdsPresents != null ? etudiantIdsPresents : List.of());

            if (user instanceof Enseignant e) {
                req.setEnseignantId(e.getId());
            }

            appelsService.enregistrerAppelManuel(req);
            redirectAttributes.addFlashAttribute("succes", "✅ Présences enregistrées avec succès !");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", "❌ Erreur : " + e.getMessage());
        }

        return "redirect:/enseignant/appels/" + id + "/appel";  // ✅ Redirection correcte
    }

    /**
     * POST /enseignant/appels/{id}/marquer-retard
     * Marque un retard pour un étudiant
     */
    @PostMapping("/{id}/marquer-retard")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ASSISTANT')")
    public String marquerRetard(@PathVariable Long id,
                                @RequestParam Long etudiantId,
                                @RequestParam LocalTime heureArrivee,
                                @AuthenticationPrincipal Utilisateur user,
                                RedirectAttributes redirectAttributes) {
        try {
            AppelRetardRequest req = new AppelRetardRequest();
            req.setEtudiantId(etudiantId);
            req.setPlageHoraireId(id);  // ✅ Utilise le path variable
            req.setHeureArrivee(heureArrivee);

            if (user instanceof Enseignant e) {
                req.setEnseignantId(e.getId());
            }

            appelsService.marquerRetard(req);
            redirectAttributes.addFlashAttribute("succes", "⏰ Retard enregistré !");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", "❌ Erreur : " + e.getMessage());
        }

        return "redirect:/enseignant/appels/" + id + "/appel";  // ✅
    }

    /**
     * POST /enseignant/appels/{id}/lancer-session
     * Lance une session d'appel (QR Code ou PIN)
     */
    @PostMapping("/{id}/lancer-session")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ASSISTANT')")
    public String lancerSession(@PathVariable Long id,
                                @RequestParam String methode,
                                @RequestParam(defaultValue = "3") int dureeMinutes,
                                @AuthenticationPrincipal Utilisateur user,
                                RedirectAttributes redirectAttributes) {
        try {
            SessionAppelRequest req = new SessionAppelRequest();
            req.setPlageHoraireId(id);  // ✅ Utilise le path variable
            req.setMethode(MethodeValidation.valueOf(methode));
            req.setDureeMinutes(dureeMinutes);

            sessionAppelService.creer(req, user.getId());
            redirectAttributes.addFlashAttribute("succes", "📱 Session " + methode + " lancée !");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", "❌ Erreur : " + e.getMessage());
        }

        return "redirect:/enseignant/appels/" + id + "/appel";  // ✅
    }

    /**
     * POST /enseignant/appels/{id}/arreter-session/{sid}
     * Arrête une session d'appel
     */
    @PostMapping("/{id}/arreter-session/{sid}")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ASSISTANT')")
    public String arreterSession(@PathVariable Long id,
                                 @PathVariable Long sid,
                                 RedirectAttributes redirectAttributes) {
        try {
            sessionAppelService.arreterSession(sid);
            redirectAttributes.addFlashAttribute("succes", "⏹️ Session arrêtée.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", "❌ Erreur : " + e.getMessage());
        }

        return "redirect:/enseignant/appels/" + id + "/appel";  // ✅
    }

    /**
     * POST /enseignant/appels/{id}/terminer/{sid}
     * Termine le cours
     */
    @PostMapping("/{id}/terminer/{sid}")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ASSISTANT')")
    public String terminerCours(@PathVariable Long id,
                                @PathVariable Long sid,
                                RedirectAttributes redirectAttributes) {
        try {
            sessionAppelService.terminerCours(sid);
            redirectAttributes.addFlashAttribute("succes", "🏁 Cours terminé !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", "❌ Erreur : " + e.getMessage());
        }

        return "redirect:/enseignant/appels/" + id + "/appel";  // ✅ CORRECTION ICI : était "/" + id + "/" + id
    }

    /**
     * POST /enseignant/appels/{id}/ajuster-heures/{aid}
     * Ajuste les heures de présence d'un étudiant
     */
    @PostMapping("/{id}/ajuster-heures/{aid}")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ASSISTANT')")
    public String ajusterHeures(@PathVariable Long id,
                                @PathVariable Long aid,
                                @RequestParam int nbHeuresPresent,
                                RedirectAttributes redirectAttributes) {
        try {
            appelsService.ajusterHeures(aid, nbHeuresPresent);
            redirectAttributes.addFlashAttribute("succes", "✅ Heures ajustées !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", "❌ Erreur : " + e.getMessage());
        }

        return "redirect:/enseignant/appels/" + id + "/appel";  // ✅
    }
}