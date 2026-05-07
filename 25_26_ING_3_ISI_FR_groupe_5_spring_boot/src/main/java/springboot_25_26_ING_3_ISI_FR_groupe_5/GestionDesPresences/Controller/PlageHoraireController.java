package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.PlageHoraire.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Services.InterfaceService.IPlageHoraireService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple.ClassesService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.EnseignantService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Services.ServiceImple.ProgrammationUEService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple.SemestreService;

import java.sql.Time;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Classe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.PlageHoraire.PlageHoraireDragDropRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.PlageHoraire.PlageHoraireRecurrenceRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.PlageHoraire.PlageHoraireRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.PlageHoraire.PlageHoraireResponse;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.PlageHoraire;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Services.ServiceImple.PlageHoraireService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Config.Security;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Etudiant;

@Slf4j
@Controller
@RequestMapping("/enseignant/emploi-du-temps")
@RequiredArgsConstructor
public class PlageHoraireController {

    private final IPlageHoraireService plageHoraireService;
    private final ClassesService classesService;
    private final EnseignantService enseignantService;
    private final ProgrammationUEService programmationUEService;
    private final SemestreService semestreService;

    // ============================================
    // PAGE PRINCIPALE — Emploi du temps d'une classe
    // ============================================

    @GetMapping("/classe/{classeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'ADMIN_INSTITUT', 'ASSISTANT', 'ENSEIGNANT', 'ETUDIANT')")
    public String emploiDuTempsClasse(
            @PathVariable Long classeId,
            @RequestParam(required = false) Long semestreId,
            @RequestParam(required = false) LocalDate semaine,
            Model model,
            @AuthenticationPrincipal Utilisateur utilisateur) {

        if (semaine == null) {
            semaine = LocalDate.now().with(DayOfWeek.MONDAY);
        }

        LocalDate debutSemaine = semaine;
        LocalDate finSemaine = semaine.plusDays(6);

        List<PlageHoraireResponse> plages = plageHoraireService
                .getByClasseAndSemaine(classeId, debutSemaine, finSemaine);

        model.addAttribute("classe", classesService.findById(classeId));
        model.addAttribute("plages", plages);
        model.addAttribute("semaine", semaine);
        model.addAttribute("debutSemaine", debutSemaine);
        model.addAttribute("finSemaine", finSemaine);
        model.addAttribute("semainePrecedente", semaine.minusWeeks(1));
        model.addAttribute("semaineSuivante", semaine.plusWeeks(1));

        if (semestreId != null) {
            model.addAttribute("totalCours", plageHoraireService.getTotalCours(classeId, semestreId));
            model.addAttribute("totalHeures", plageHoraireService.getTotalDureeMinutes(classeId, semestreId) / 60);
        }

        model.addAttribute("semestres", semestreService.getByAnnee(classesService.getAnneeAcademiqueActive()));
        model.addAttribute("programmations", programmationUEService.getByClasse(classeId));
        model.addAttribute("enseignants", enseignantService.getAll());
        model.addAttribute("form", new PlageHoraireRequest());
        model.addAttribute("formRecurrence", new PlageHoraireRecurrenceRequest());

        return "emploi-du-temps/classe";
    }

    // ============================================
    // PAGE — Emploi du temps d'un enseignant
    // ============================================

    @GetMapping("/enseignant/{enseignantId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'ADMIN_INSTITUT', 'ASSISTANT', 'ENSEIGNANT')")
    public String emploiDuTempsEnseignant(
            @PathVariable Long enseignantId,
            @RequestParam(required = false) LocalDate semaine,
            Model model,
            @AuthenticationPrincipal Utilisateur utilisateur) {

        if (semaine == null) {
            semaine = LocalDate.now().with(DayOfWeek.MONDAY);
        }

        LocalDate debutSemaine = semaine;
        LocalDate finSemaine = semaine.plusDays(6);

        List<PlageHoraireResponse> plages = plageHoraireService
                .getByEnseignantAndSemaine(enseignantId, debutSemaine, finSemaine);

        model.addAttribute("enseignant", enseignantService.findById(enseignantId));
        model.addAttribute("plages", plages);
        model.addAttribute("semaine", semaine);
        model.addAttribute("debutSemaine", debutSemaine);
        model.addAttribute("finSemaine", finSemaine);
        model.addAttribute("semainePrecedente", semaine.minusWeeks(1));
        model.addAttribute("semaineSuivante", semaine.plusWeeks(1));
        model.addAttribute("coursAujourdhui", plageHoraireService.getCoursEnseignantAujourdhui(enseignantId));

        return "emploi-du-temps/enseignant";
    }

    // ============================================
    // CRÉER UNE PLAGE — Simple
    // ============================================

    @PostMapping("/creer")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'ADMIN_INSTITUT', 'ASSISTANT', 'ENSEIGNANT')")
    public String creer(
            @Valid @ModelAttribute("form") PlageHoraireRequest request,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal Utilisateur auteur) {

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("erreur", "Veuillez corriger les erreurs du formulaire");
            return "redirect:/emploi-du-temps/classe/" + request.getClasseId();
        }

        try {
            PlageHoraireResponse plage = plageHoraireService.creer(request, auteur);
            redirectAttributes.addFlashAttribute("succes", "✅ Séance créée : " + plage.getTitreAffiche());
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("erreur", "⚠️ " + e.getMessage());
        } catch (Exception e) {
            log.error("Erreur création plage : {}", e.getMessage());
            redirectAttributes.addFlashAttribute("erreur", "❌ Erreur lors de la création");
        }

        return "redirect:/emploi-du-temps/classe/" + request.getClasseId();
    }

    // ============================================
    // CRÉER PAR RÉCURRENCE
    // ============================================

    @PostMapping("/creer-recurrence")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'ADMIN_INSTITUT', 'ASSISTANT','ENSEIGNANT')")
    public String creerRecurrence(
            @Valid @ModelAttribute("formRecurrence") PlageHoraireRecurrenceRequest request,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal Utilisateur auteur) {

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("erreur", "Veuillez corriger les erreurs du formulaire");
            return "redirect:/emploi-du-temps/classe/" + request.getClasseId();
        }

        try {
            List<PlageHoraireResponse> plages = plageHoraireService.creerRecurrence(request, auteur);
            redirectAttributes.addFlashAttribute("succes", "✅ " + plages.size() + " séances récurrentes créées");
        } catch (Exception e) {
            log.error("Erreur récurrence : {}", e.getMessage());
            redirectAttributes.addFlashAttribute("erreur", "❌ " + e.getMessage());
        }

        return "redirect:/emploi-du-temps/classe/" + request.getClasseId();
    }

    // ============================================
    // DRAG & DROP — Créer ou déplacer (JSON)
    // ============================================

    @PostMapping("/drag-drop")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'ADMIN_INSTITUT', 'ASSISTANT', 'ENSEIGNANT')")
    @ResponseBody
    public Map<String, Object> dragDrop(
            @RequestBody PlageHoraireDragDropRequest request,
            @AuthenticationPrincipal Utilisateur auteur) {

        Map<String, Object> response = new HashMap<>();
        try {
            PlageHoraireResponse plage = plageHoraireService.creerParDragDrop(request, auteur);
            response.put("succes", true);
            response.put("id", plage.getId());
        } catch (IllegalStateException e) {
            response.put("succes", false);
            response.put("message", e.getMessage());
        } catch (Exception e) {
            log.error("Erreur drag & drop : {}", e.getMessage());
            response.put("succes", false);
            response.put("message", "Erreur serveur");
        }
        return response;
    }

    // ============================================
    // DÉPLACER UNE PLAGE EXISTANTE (JSON)
    // ============================================

    @PostMapping("/{id}/deplacer")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'ADMIN_INSTITUT', 'ASSISTANT', 'ENSEIGNANT')")
    @ResponseBody
    public Map<String, Object> deplacer(
            @PathVariable Long id,
            @RequestBody PlageHoraireDragDropRequest request,
            @AuthenticationPrincipal Utilisateur auteur) {

        Map<String, Object> response = new HashMap<>();
        try {
            PlageHoraireResponse plage = plageHoraireService.deplacer(id, request, auteur);
            response.put("succes", true);
            response.put("id", plage.getId());
        } catch (IllegalStateException e) {
            response.put("succes", false);
            response.put("message", e.getMessage());
        } catch (Exception e) {
            log.error("Erreur déplacement : {}", e.getMessage());
            response.put("succes", false);
            response.put("message", "Erreur serveur");
        }
        return response;
    }

    // ============================================
    // AFFECTER DES ENSEIGNANTS
    // ============================================

    @PostMapping("/{id}/enseignants")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'ADMIN_INSTITUT', 'ASSISTANT')")
    public String affecterEnseignants(
            @PathVariable Long id,
            @RequestParam List<Long> enseignantIds,
            @RequestParam Long classeId,
            RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal Utilisateur auteur) {

        try {
            plageHoraireService.affecterEnseignants(id, enseignantIds, auteur);
            redirectAttributes.addFlashAttribute("succes", "✅ Enseignants affectés avec succès");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("erreur", "⚠️ " + e.getMessage());
        } catch (Exception e) {
            log.error("Erreur affectation : {}", e.getMessage());
            redirectAttributes.addFlashAttribute("erreur", "❌ Erreur lors de l'affectation");
        }

        return "redirect:/emploi-du-temps/classe/" + classeId;
    }

    // ============================================
    // MODIFIER UNE PLAGE (JSON) ✅ CORRIGÉ
    // ============================================

    @PostMapping("/{id}/modifier")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'ADMIN_INSTITUT', 'ASSISTANT', 'ENSEIGNANT')")  // 🆕 Enseignant peut aussi modifier
    @ResponseBody
    public Map<String, Object> modifier(
            @PathVariable Long id,
            @RequestParam(required = false) Long classeId,
            @RequestParam(required = false) String jour,
            @RequestParam(required = false) String heureDebut,
            @RequestParam(required = false) String heureFin,
            @RequestParam(required = false) String couleur,
            @RequestParam(required = false) String salle,
            @RequestParam(required = false) List<Long> enseignantIds,
            @AuthenticationPrincipal Utilisateur auteur) {

        Map<String, Object> response = new HashMap<>();
        try {
            PlageHoraireRequest request = new PlageHoraireRequest();
            request.setClasseId(classeId);
            request.setJour(LocalDate.parse(jour));
            request.setHeureDebut(Time.valueOf(heureDebut).toLocalTime());
            request.setHeureFin(Time.valueOf(heureFin).toLocalTime());
            request.setCouleur(couleur);
            request.setSalle(salle);
            request.setEnseignantIds(enseignantIds);

            PlageHoraireResponse plage = plageHoraireService.modifier(id, request, auteur);
            response.put("succes", true);
            response.put("id", plage.getId());
        } catch (IllegalStateException e) {
            response.put("succes", false);
            response.put("message", e.getMessage());
        } catch (Exception e) {
            log.error("Erreur modification : {}", e.getMessage());
            response.put("succes", false);
            response.put("message", "Erreur serveur");
        }
        return response;
    }

    // ============================================
    // SUPPRIMER UNE PLAGE (JSON) ✅ CORRIGÉ
    // ============================================

    @PostMapping("/{id}/supprimer")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'ADMIN_INSTITUT', 'ASSISTANT', 'ENSIGNANT')")
    @ResponseBody
    public Map<String, Object> supprimer(
            @PathVariable Long id,
            @RequestParam Long classeId,
            @AuthenticationPrincipal Utilisateur auteur) {

        Map<String, Object> response = new HashMap<>();
        try {
            plageHoraireService.supprimer(id, auteur);
            response.put("succes", true);
            response.put("message", "Séance supprimée");
        } catch (Exception e) {
            log.error("Erreur suppression : {}", e.getMessage());
            response.put("succes", false);
            response.put("message", e.getMessage());
        }
        return response;
    }

    // ============================================
    // DÉTAIL D'UNE PLAGE
    // ============================================

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public String detail(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            PlageHoraireResponse plage = plageHoraireService.findById(id);
            model.addAttribute("plage", plage);
            model.addAttribute("enseignants", enseignantService.getAll());
            return "emploi-du-temps/detail";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", "Séance introuvable");
            return "redirect:/dashboard";
        }
    }
}