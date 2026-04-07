package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Controller.AdminController;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Semestre;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Enums.TypeSemestre;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.SemestreService;

import java.time.LocalDate;

@Controller
@RequestMapping("/semestres")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class SemestreController {

    private final SemestreService semestreService;

    @PostMapping("/creer")
    public String creer(
            @RequestParam TypeSemestre typeSemestre,
            @RequestParam Long anneeAcademiqueId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateDebut,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateFin,
            @RequestParam(defaultValue = "false") boolean actif,
            RedirectAttributes redirectAttributes
    ) {
        try {
            semestreService.creer(typeSemestre, anneeAcademiqueId, dateDebut, dateFin, actif);
            redirectAttributes.addFlashAttribute("succes", "Semestre " + typeSemestre.getLibelle() + " créé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/annees/" + anneeAcademiqueId;
    }

    @PostMapping("/{id}/activer")
    public String activer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Semestre semestre = semestreService.activerSemestre(id);
            redirectAttributes.addFlashAttribute("succes", "Semestre activé avec succès");
            return "redirect:/annees/" + semestre.getAnneeAcademique().getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
            return "redirect:/annees";
        }
    }

    @PostMapping("/{id}/desactiver")
    public String desactiver(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Semestre semestre = semestreService.desactiverSemestre(id);
            redirectAttributes.addFlashAttribute("succes", "Semestre désactivé avec succès");
            return "redirect:/annees/" + semestre.getAnneeAcademique().getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
            return "redirect:/annees";
        }
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Semestre semestre = semestreService.findById(id);
            Long anneeId = semestre.getAnneeAcademique().getId();
            semestreService.supprimer(id);
            redirectAttributes.addFlashAttribute("succes", "Semestre supprimé avec succès");
            return "redirect:/annees/" + anneeId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
            return "redirect:/annees";
        }
    }
}