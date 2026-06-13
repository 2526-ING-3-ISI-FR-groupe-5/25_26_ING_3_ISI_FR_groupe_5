package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Annee_academique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Repository.AnneeAcademiqueRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Repository.ClassesRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple.AnneeAcademiqueService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple.InstitutSecurityService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.ProgrammationUE;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Services.ServiceImple.ProgrammationUEService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.utilisateur.UtilisateurRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.utilisateur.UtilisateurResponse;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exception.EmailAlreadyUsedException;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exception.UserNotFoundException;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.RoleRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.UtilisateurRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.UtilisateurService;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/admin/utilisateurs")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN_INSTITUT')")
public class AdminUtilisateurController {

    private final UtilisateurService utilisateurService;
    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final ClassesRepository classesRepository;
    private final AnneeAcademiqueService anneeService;
    private final ProgrammationUEService programmationService;
    private final AnneeAcademiqueRepository anneeRepository;
    private final InstitutSecurityService securityService;

    // ══════════════════════════════════════════
    // LISTE
    // ══════════════════════════════════════════

    @GetMapping
    public String lister(
            Model model,
            @RequestParam(defaultValue = "") String recherche,
            @RequestParam(defaultValue = "TOUS") String type,
            @RequestParam(required = false) Long anneeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // 1. Charger toutes les années disponibles pour le sélecteur
        List<Annee_academique> toutesLesAnnees = anneeService.getAll();

        // 2. Résoudre l'année sélectionnée : param URL > année active > null
        Annee_academique anneeSelectionnee = null;
        Long anneeIdEffectif = anneeId;
        if (anneeIdEffectif != null) {
            final Long anneeIdFinal = anneeIdEffectif;
            anneeSelectionnee = toutesLesAnnees.stream()
                    .filter(a -> a.getId().equals(anneeIdFinal))
                    .findFirst().orElse(null);
        }
        if (anneeSelectionnee == null) {
            try {
                anneeSelectionnee = anneeService.getAnneeActive();
                // Ne pas forcer anneeIdEffectif : l'année active sert uniquement à l'affichage du badge.
                // Forcer l'id filtrerait les enseignants sans programmation et masquerait des utilisateurs.
            } catch (Exception e) {
                log.warn("Aucune année active disponible: {}", e.getMessage());
            }
        }

        // 3. Charger la liste filtrée + total réel du personnel de l'institut
        Page<UtilisateurResponse> pageResult = utilisateurService.listeTous(recherche, type, anneeIdEffectif, page, size);
        Long institutId = securityService.getInstitutIdCourant();
        long totalPersonnel = utilisateurRepository.countPersonnelByInstitut(institutId);

        // 4. Modèle
        model.addAttribute("utilisateurs", pageResult);
        model.addAttribute("totalPersonnel", totalPersonnel);
        model.addAttribute("recherche", recherche);
        model.addAttribute("typeSelectionne", type);
        model.addAttribute("anneeId", anneeIdEffectif);
        model.addAttribute("anneeSelectionnee", anneeSelectionnee);
        model.addAttribute("toutesLesAnnees", toutesLesAnnees);
        model.addAttribute("anneeActive", anneeSelectionnee);

        return "Utilisateurs/liste";
    }

    // ══════════════════════════════════════════
    // DÉTAIL
    // ══════════════════════════════════════════

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur introuvable : " + id));

        if (utilisateur instanceof Enseignant) {
            model.addAttribute("details", utilisateurService.EnsDetails(id));
            model.addAttribute("type", "ENSEIGNANT");
        } else if (utilisateur instanceof AssistantPedagogique) {
            model.addAttribute("details", utilisateurService.AssDetails(id));
            model.addAttribute("type", "ASSISTANT");
        } else if (utilisateur instanceof Surveillant) {
            model.addAttribute("details", utilisateurService.SurDetails(id));
            model.addAttribute("type", "SURVEILLANT");
        } else {
            return "redirect:/admin/utilisateurs";
        }
        return "Utilisateurs/detail";
    }

    // ══════════════════════════════════════════
    // FORMULAIRE CRÉATION
    // ══════════════════════════════════════════

    @GetMapping("/creer")
    public String formulaireCreation(Model model) {
        Annee_academique anneeActive = anneeService.getAnneeActive();
        List<ProgrammationUE> programmations = (anneeActive != null)
                ? programmationService.getByAnnee(anneeActive.getId())
                : List.of();

        model.addAttribute("roles", roleRepository.findByActive(true));
        model.addAttribute("classes", classesRepository.findAll());
        model.addAttribute("programmations", programmations);
        model.addAttribute("anneeActive", anneeActive);
        model.addAttribute("request", new UtilisateurRequest());
        return "Utilisateurs/createUser";
    }

    // ══════════════════════════════════════════
    // CRÉER
    // ══════════════════════════════════════════

    @PostMapping("/creer")
    public String creer(
            @ModelAttribute UtilisateurRequest request,
            RedirectAttributes ra) {
        try {
            utilisateurService.creerUtilisateur(request);
            ra.addFlashAttribute("success",
                    "Utilisateur " + request.getPrenom() + " " + request.getNom() + " cree avec succes.");
        } catch (EmailAlreadyUsedException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/utilisateurs/creer";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Erreur lors de la creation : " + e.getMessage());
            return "redirect:/admin/utilisateurs/creer";
        }
        return "redirect:/admin/utilisateurs";
    }

    // ══════════════════════════════════════════
    // ACTIVER / DÉSACTIVER
    // ══════════════════════════════════════════

    @PostMapping("/{id}/toggle-active")
    public String toggleActive(@PathVariable Long id, @RequestParam boolean active, RedirectAttributes ra) {
        try {
            utilisateurService.activerDesactiverUtilisateur(id, active);
            ra.addFlashAttribute("success", active ? "Utilisateur active." : "Utilisateur desactive.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/utilisateurs";
    }

    // ══════════════════════════════════════════
    // SUPPRIMER
    // ══════════════════════════════════════════

    @PostMapping("/{id}/supprimer")
    public String supprimer(@PathVariable Long id, RedirectAttributes ra) {
        try {
            utilisateurService.deleteUtilisateur(id);
            ra.addFlashAttribute("success", "Utilisateur supprime.");
        } catch (UserNotFoundException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/utilisateurs";
    }
}