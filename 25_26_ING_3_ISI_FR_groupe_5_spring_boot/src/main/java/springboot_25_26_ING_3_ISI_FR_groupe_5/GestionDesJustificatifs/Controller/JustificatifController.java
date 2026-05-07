package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesJustificatifs.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesJustificatifs.DTO.justificatif.JustificatifRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesJustificatifs.Services.ServiceImple.JustificatifService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple.InstitutSecurityService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.EtudiantService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesJustificatifs.Enum.TypeJustificatif;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesJustificatifs.Entity.Justificatif;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Config.Security;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Etudiant;

@Controller
@RequestMapping("enseignant/justificatifs")
@RequiredArgsConstructor
public class JustificatifController {

    private final JustificatifService justificatifService;
    private final InstitutSecurityService securityService;
    private final EtudiantService etudiantService;

    // ══════════════════════════════════════════
    // ÉTUDIANT — liste de ses justificatifs
    // ══════════════════════════════════════════

    @GetMapping("/mes-justificatifs")
    @PreAuthorize("hasRole('ETUDIANT')")
    public String mesJustificatifs(@AuthenticationPrincipal Utilisateur utilisateur,
                                   Model model) {
        model.addAttribute("justificatifs",
                justificatifService.getByEtudiant(utilisateur.getId()));
        return "justificatif/liste";
    }

    // ══════════════════════════════════════════
    // ÉTUDIANT — formulaire soumission
    // ══════════════════════════════════════════

    @GetMapping("/soumettre")
    @PreAuthorize("hasRole('ETUDIANT')")
    public String formulaireSoumettre(@AuthenticationPrincipal Utilisateur utilisateur,
                                      Model model) {
        JustificatifRequest request = new JustificatifRequest();
        request.setEtudiantId(utilisateur.getId());
        model.addAttribute("justificatifRequest", request);
        model.addAttribute("typesJustificatif", TypeJustificatif.values());
        return "justificatif/soumettre";
    }

    @PostMapping("/soumettre")
    @PreAuthorize("hasRole('ETUDIANT')")
    public String soumettre(@Valid @ModelAttribute("justificatifRequest") JustificatifRequest request,
                            BindingResult result,
                            @AuthenticationPrincipal Utilisateur utilisateur,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("typesJustificatif", TypeJustificatif.values());
            return "justificatif/soumettre";
        }
        try {
            request.setEtudiantId(utilisateur.getId());
            justificatifService.soumettre(request, utilisateur);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Votre justificatif a été soumis avec succès.");
            return "redirect:/justificatifs/mes-justificatifs";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("typesJustificatif", TypeJustificatif.values());
            return "justificatif/soumettre";
        }
    }

    // ══════════════════════════════════════════
    // ASSISTANT — liste des justificatifs en attente
    // ══════════════════════════════════════════

    @GetMapping("/assistant")
    @PreAuthorize("hasAnyRole('ASSISTANT', 'ADMIN_INSTITUT', 'SUPER_ADMIN')")
    public String listeAssistant(Model model) {
        Long institutId = securityService.getInstitutIdCourant();
        model.addAttribute("justificatifs",
                institutId != null
                        ? justificatifService.getEnAttenteByInstitut(institutId)
                        : justificatifService.getEnAttenteByInstitut(null));
        return "justificatif/listeAssistant";
    }

    // ══════════════════════════════════════════
    // ASSISTANT — détail d'un justificatif
    // ══════════════════════════════════════════

    @GetMapping("/assistant/{id}")
    @PreAuthorize("hasAnyRole('ASSISTANT', 'ADMIN_INSTITUT', 'SUPER_ADMIN')")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("justificatif", justificatifService.findById(id));
        return "justificatif/detail";
    }

    // ══════════════════════════════════════════
    // ASSISTANT — valider
    // ══════════════════════════════════════════

    @PostMapping("/assistant/{id}/valider")
    @PreAuthorize("hasAnyRole('ASSISTANT', 'ADMIN_INSTITUT', 'SUPER_ADMIN')")
    public String valider(@PathVariable Long id,
                          @RequestParam(required = false) String commentaire,
                          @AuthenticationPrincipal Utilisateur validateur,
                          RedirectAttributes redirectAttributes) {
        try {
            justificatifService.valider(id, validateur, commentaire);
            redirectAttributes.addFlashAttribute("successMessage", "Justificatif validé.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/justificatifs/assistant/" + id;
    }

    // ══════════════════════════════════════════
    // ASSISTANT — refuser
    // ══════════════════════════════════════════

    @PostMapping("/assistant/{id}/refuser")
    @PreAuthorize("hasAnyRole('ASSISTANT', 'ADMIN_INSTITUT', 'SUPER_ADMIN')")
    public String refuser(@PathVariable Long id,
                          @RequestParam(required = false) String commentaire,
                          @AuthenticationPrincipal Utilisateur validateur,
                          RedirectAttributes redirectAttributes) {
        try {
            justificatifService.refuser(id, validateur, commentaire);
            redirectAttributes.addFlashAttribute("errorMessage", "Justificatif refusé.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/justificatifs/assistant/" + id;
    }

    // ══════════════════════════════════════════
    // COMMUN — supprimer
    // ══════════════════════════════════════════

    @PostMapping("/{id}/supprimer")
    @PreAuthorize("hasAnyRole('ETUDIANT', 'ADMIN_INSTITUT', 'SUPER_ADMIN')")
    public String supprimer(@PathVariable Long id,
                            @AuthenticationPrincipal Utilisateur utilisateur,
                            RedirectAttributes redirectAttributes) {
        try {
            justificatifService.supprimer(id, utilisateur);
            redirectAttributes.addFlashAttribute("successMessage", "Justificatif supprimé.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        boolean isEtudiant = utilisateur.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ETUDIANT"));
        return isEtudiant
                ? "redirect:/justificatifs/mes-justificatifs"
                : "redirect:/justificatifs/assistant";
    }
}