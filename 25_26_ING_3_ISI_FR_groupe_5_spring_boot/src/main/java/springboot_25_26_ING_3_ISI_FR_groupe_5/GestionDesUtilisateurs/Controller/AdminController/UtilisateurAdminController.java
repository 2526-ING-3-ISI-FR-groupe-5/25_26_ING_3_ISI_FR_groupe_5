package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Controller.AdminController;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers.ClassesMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers.UEMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers.UtilisateurMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.UtilisateurRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.AnneeAcademiqueService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.ClassesService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.UEService;

@Controller
@RequestMapping("/admin/utilisateurs")
@RequiredArgsConstructor
public class UtilisateurAdminController {

    private final UtilisateurRepository utilisateurRepo;
    private final UtilisateurMapper utilisateurMapper;
    private final ClassesService classesService;
    private final ClassesMapper classesMapper;
    private final UEService ueService;
    private final UEMapper ueMapper;
    private final AnneeAcademiqueService anneeService;

    // ══════════════════════════════════════════
    // LISTE
    // ══════════════════════════════════════════
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String liste(
            @RequestParam(required = false) String recherche,
            @RequestParam(required = false, defaultValue = "TOUS") String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            Model model
    ) {
        // Récupérer TOUS les utilisateurs paginés
        Page<Utilisateur> utilisateurs = utilisateurRepo.findAll(PageRequest.of(page, size));

        model.addAttribute("utilisateurs", utilisateurs.map(utilisateurMapper::toDTO));
        model.addAttribute("recherche", recherche);
        model.addAttribute("typeSelectionne", type);

        // Pour le modal "Ajouter utilisateur"
        try {
            var annee = anneeService.getAnneeActive();
            model.addAttribute("classes",
                    classesMapper.toResponseList(
                            classesService.getByAnnee(annee.getId(), null, PageRequest.of(0, 200)).getContent()
                    ));
        } catch (Exception e) {
            model.addAttribute("classes", java.util.List.of());
        }
        model.addAttribute("ues", ueMapper.toResponseList(ueService.getAll()));

        return "Utilisateurs/liste";
    }

    // ══════════════════════════════════════════
    // SUPPRIMER
    // ══════════════════════════════════════════
    @PostMapping("/supprimer")
    @PreAuthorize("hasRole('ADMIN')")
    public String supprimer(
            @RequestParam Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            utilisateurRepo.deleteById(id);
            redirectAttributes.addFlashAttribute("succes", "Utilisateur supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/admin/utilisateurs";
    }

    // ══════════════════════════════════════════
    // DÉTAILS (redirection vers le bon contrôleur selon type)
    // ══════════════════════════════════════════
    @GetMapping("/details/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String details(@PathVariable Long id, Model model) {
        Utilisateur u = utilisateurRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        model.addAttribute("utilisateur", utilisateurMapper.toDTO(u));
        return "Utilisateurs/detail";
    }
}
