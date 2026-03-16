package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Response.UtilisateurResponseDTO;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.AssistantPedagogique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exceptions.UserNotFoundException;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.UtilisateurRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceAdminImpl;

@Controller
@RequiredArgsConstructor
public class AdminController {
  private final UtilisateurRepository utilisateurRepository;
    private  final ServiceAdminImpl  serviceAdmin;
    @GetMapping("/admin/utilisateurs")
    public String listerUtilisateurs(
            Model model,
            @RequestParam(defaultValue = "") String recherche,
            @RequestParam(defaultValue = "TOUS") String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Page<UtilisateurResponseDTO> pageResult = serviceAdmin.listeTous(recherche, type, page, size);

        model.addAttribute("utilisateurs", pageResult);
        model.addAttribute("recherche", recherche);
        model.addAttribute("typeSelectionne", type);

        return "utilisateurs";
    }

    @GetMapping("/admin/utilisateurs/details/{id}")
    public String voirDetails(@PathVariable Long id, Model model) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur introuvable"));

        if (utilisateur instanceof Enseignant) {
            model.addAttribute("details", serviceAdmin.EnsDetails(id));
            model.addAttribute("type", "ENSEIGNANT");
        } else if (utilisateur instanceof AssistantPedagogique) {
            model.addAttribute("details", serviceAdmin.AssDetails(id));
            model.addAttribute("type", "ASSISTANT");
        } else {
            return "redirect:/admin/utilisateurs?error=TypeInconnu";
        }

        return "details1"; // ← une seule page
    }
}
