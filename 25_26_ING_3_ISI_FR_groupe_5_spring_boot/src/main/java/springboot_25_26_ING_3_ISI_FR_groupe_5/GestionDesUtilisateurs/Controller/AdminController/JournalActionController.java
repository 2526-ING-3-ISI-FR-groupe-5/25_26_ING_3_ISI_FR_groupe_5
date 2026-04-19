package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Controller.AdminController;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.journal.JournalActionResponse;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.StatutAction;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.TypeAction;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService.IJournalActionService;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/journal")
@RequiredArgsConstructor
public class JournalActionController {

    private final IJournalActionService journalActionService;

    // ============================================
    // Liste complète — Admin uniquement
    // GET /journal
    // ============================================

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String liste(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long utilisateurId,
            @RequestParam(required = false) TypeAction typeAction,
            @RequestParam(required = false) StatutAction statut,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime debut,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fin) {

        Pageable pageable = PageRequest.of(
                page, size, Sort.by("dateAction").descending()
        );

        Page<JournalActionResponse> journaux = journalActionService.search(
                utilisateurId, typeAction, statut, debut, fin, pageable
        );

        // ✅ Données pour la vue
        model.addAttribute("journaux", journaux);
        model.addAttribute("typesAction", TypeAction.values());
        model.addAttribute("statuts", StatutAction.values());

        // ✅ Conserver les filtres dans la vue
        model.addAttribute("utilisateurIdFiltre", utilisateurId);
        model.addAttribute("typeActionFiltre", typeAction);
        model.addAttribute("statutFiltre", statut);
        model.addAttribute("debutFiltre", debut);
        model.addAttribute("finFiltre", fin);

        // ✅ Pagination
        model.addAttribute("pageActuelle", page);
        model.addAttribute("totalPages", journaux.getTotalPages());
        model.addAttribute("totalElements", journaux.getTotalElements());

        return "journal/liste";
    }

    // ============================================
    // Détail d'une action
    // GET /journal/{id}
    // ============================================

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String detail(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            JournalActionResponse journal = journalActionService.getById(id);
            model.addAttribute("journal", journal);
            return "journal/detail";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "erreur",
                    "Action introuvable"
            );
            return "redirect:/journal";
        }
    }

    // ============================================
    // Historique d'un utilisateur spécifique
    // GET /journal/utilisateur/{utilisateurId}
    // Admin → peut voir n'importe quel utilisateur
    // ============================================

    @GetMapping("/utilisateur/{utilisateurId}")
    @PreAuthorize("hasRole('ADMIN') or #utilisateurId == authentication.principal.id")
    public String historiqueUtilisateur(
            @PathVariable Long utilisateurId,
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(
                page, size, Sort.by("dateAction").descending()
        );

        Page<JournalActionResponse> journaux = journalActionService
                .getByUtilisateur(utilisateurId, pageable);

        model.addAttribute("journaux", journaux);
        model.addAttribute("utilisateurId", utilisateurId);
        model.addAttribute("pageActuelle", page);
        model.addAttribute("totalPages", journaux.getTotalPages());
        model.addAttribute("totalElements", journaux.getTotalElements());

        return "journal/historique-utilisateur";
    }

    // ============================================
    // Mon historique — Utilisateur connecté
    // GET /journal/mon-historique
    // ============================================

    @GetMapping("/mon-historique")
    @PreAuthorize("isAuthenticated()")
    public String monHistorique(
            @AuthenticationPrincipal Utilisateur utilisateur,
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(
                page, size, Sort.by("dateAction").descending()
        );

        Page<JournalActionResponse> journaux = journalActionService
                .getByUtilisateur(utilisateur.getId(), pageable);

        model.addAttribute("journaux", journaux);
        model.addAttribute("utilisateur", utilisateur);
        model.addAttribute("pageActuelle", page);
        model.addAttribute("totalPages", journaux.getTotalPages());
        model.addAttribute("totalElements", journaux.getTotalElements());

        return "journal/mon-historique";
    }

    // ============================================
    // Statistiques — Admin uniquement
    // GET /journal/stats
    // ============================================

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public String stats(Model model) {

        model.addAttribute("statsByType",
                journalActionService.getStatsByType());
        model.addAttribute("statsByEchecs",
                journalActionService.getStatsByEchecs());

        return "journal/stats";
    }

    // ============================================
    // IPs suspectes — Admin uniquement
    // GET /journal/securite
    // ============================================

    @GetMapping("/securite")
    @PreAuthorize("hasRole('ADMIN')")
    public String securite(
            Model model,
            @RequestParam(defaultValue = "5") Long seuil,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime depuis) {

        if (depuis == null) {
            depuis = LocalDateTime.now().minusHours(24);
        }

        model.addAttribute("ipsSuspectes",
                journalActionService.getIpsSuspectes(depuis, seuil));
        model.addAttribute("seuil", seuil);
        model.addAttribute("depuis", depuis);

        return "journal/securite";
    }
}