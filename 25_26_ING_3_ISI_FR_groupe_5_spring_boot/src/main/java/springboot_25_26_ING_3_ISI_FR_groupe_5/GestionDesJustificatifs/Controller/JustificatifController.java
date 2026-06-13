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
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesJustificatifs.DTO.justificatif.JustificatifResponse; // AJOUT
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesJustificatifs.Mappers.JustificatifMapper; // AJOUT
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesJustificatifs.Entity.Justificatif;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesJustificatifs.Services.InterfaceService.IJustificatifService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple.InstitutSecurityService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.EtudiantService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesJustificatifs.Enum.TypeJustificatif;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Repository.AppelsRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Enum.StatutPresence;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.Appels;

import java.util.List;

@Controller
@RequestMapping("/justificatifs")
@RequiredArgsConstructor
public class JustificatifController {

    private final IJustificatifService justificatifService;
    private final InstitutSecurityService securityService;
    private final EtudiantService etudiantService;
    private final AppelsRepository appelsRepository;
    private final JustificatifMapper justificatifMapper; // <--- INJECTÉ

    // ══════════════════════════════════════════
    // ÉTUDIANT — liste de ses justificatifs
    // ══════════════════════════════════════════
    @GetMapping("/mes-justificatifs")
    @PreAuthorize("hasRole('ETUDIANT')")
    public String mesJustificatifs(@AuthenticationPrincipal Utilisateur utilisateur, Model model) {
        List<Justificatif> entites = justificatifService.getByEtudiant(utilisateur.getId());

        // Conversion des entités brutes en DTOs avant envoi à la vue
        List<JustificatifResponse> dtos = justificatifMapper.toResponseList(entites);
        model.addAttribute("justificatifs", dtos);
        return "justificatif/liste";
    }

    // ══════════════════════════════════════════
    // ÉTUDIANT — formulaire soumission
    // ══════════════════════════════════════════
    @GetMapping("/soumettre")
    @PreAuthorize("hasRole('ETUDIANT')")
    public String formulaireSoumettre(@AuthenticationPrincipal Utilisateur utilisateur, Model model) {
        JustificatifRequest request = new JustificatifRequest();
        request.setEtudiantId(utilisateur.getId());

        // Charger uniquement les absences ou retards non régularisés de l'étudiant
        List<Appels> absences = appelsRepository.findAbsencesNonJustifieesByEtudiant(
                utilisateur.getId(),
                List.of(StatutPresence.ABSENT, StatutPresence.RETARD)
        );

        model.addAttribute("justificatifRequest", request);
        model.addAttribute("typesJustificatif", TypeJustificatif.values());
        model.addAttribute("absencesNonJustifiees", absences);
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
            model.addAttribute("absencesNonJustifiees", appelsRepository.findAbsencesNonJustifieesByEtudiant(
                    utilisateur.getId(), List.of(StatutPresence.ABSENT, StatutPresence.RETARD)));
            return "justificatif/soumettre";
        }
        try {
            request.setEtudiantId(utilisateur.getId());
            justificatifService.soumettre(request, utilisateur);
            redirectAttributes.addFlashAttribute("successMessage", "Votre justificatif a été soumis avec succès.");
            return "redirect:/justificatifs/mes-justificatifs";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("typesJustificatif", TypeJustificatif.values());
            model.addAttribute("absencesNonJustifiees", appelsRepository.findAbsencesNonJustifieesByEtudiant(
                    utilisateur.getId(), List.of(StatutPresence.ABSENT, StatutPresence.RETARD)));
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
        List<Justificatif> entites = (institutId != null)
                ? justificatifService.getEnAttenteByInstitut(institutId)
                : justificatifService.getEnAttenteByInstitut(null);

        // Conversion en DTOs pour le tableau de l'assistant
        List<JustificatifResponse> dtos = justificatifMapper.toResponseList(entites);
        model.addAttribute("justificatifs", dtos);
        return "justificatif/listeAssistant";
    }

    // ══════════════════════════════════════════
    // ASSISTANT — détail d'un justificatif
    // ══════════════════════════════════════════
    @GetMapping("/assistant/{id}")
    @PreAuthorize("hasAnyRole('ASSISTANT', 'ADMIN_INSTITUT', 'SUPER_ADMIN')")
    public String detail(@PathVariable Long id, Model model) {
        Justificatif entite = justificatifService.findById(id);

        // Conversion en DTO individuel pour la fiche de détail (et la liste des cours régularisés)
        JustificatifResponse dto = justificatifMapper.toResponse(entite);
        model.addAttribute("justificatif", dto);
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
            redirectAttributes.addFlashAttribute("successMessage", "Justificatif refusé.");
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