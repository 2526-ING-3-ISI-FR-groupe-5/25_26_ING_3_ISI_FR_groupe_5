package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.RoleService;

@Slf4j
@Controller
@RequestMapping("/admin/roles")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN_INSTITUT')")
public class AdminRoleController {

    private final RoleService roleService;

    // ══════════════════════════════════════════
    // LISTE
    // ══════════════════════════════════════════

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("roles", roleService.listeRoles());
        model.addAttribute("permissions", roleService.listePermissions());
        return "roles/liste";
    }

    // ══════════════════════════════════════════
    // ACTIVER / DÉSACTIVER RÔLE
    // ══════════════════════════════════════════

    @PostMapping("/{id}/toggle")
    public String toggleRole(@PathVariable Long id, @RequestParam boolean active, RedirectAttributes ra) {
        try {
            roleService.activerRole(id, active);
            ra.addFlashAttribute("success", active ? "Role active." : "Role desactive.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/roles";
    }

    // ══════════════════════════════════════════
    // ACTIVER / DÉSACTIVER PERMISSION GLOBALE
    // ══════════════════════════════════════════

    @PostMapping("/permissions/{id}/toggle")
    public String togglePermission(@PathVariable Long id, @RequestParam boolean active, RedirectAttributes ra) {
        try {
            roleService.activerPermission(id, active);
            ra.addFlashAttribute("success", active ? "Permission activee." : "Permission desactivee.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/roles";
    }

    // ══════════════════════════════════════════
    // AJOUTER PERMISSION À UN RÔLE
    // ══════════════════════════════════════════

    @PostMapping("/{roleId}/permissions/{permissionId}/ajouter")
    public String ajouterPermission(@PathVariable Long roleId, @PathVariable Long permissionId, RedirectAttributes ra) {
        try {
            roleService.ajouterPermission(roleId, permissionId);
            ra.addFlashAttribute("success", "Permission ajoutee au role.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/roles";
    }

    // ══════════════════════════════════════════
    // RETIRER PERMISSION D'UN RÔLE
    // ══════════════════════════════════════════

    @PostMapping("/{roleId}/permissions/{permissionId}/retirer")
    public String retirerPermission(@PathVariable Long roleId, @PathVariable Long permissionId, RedirectAttributes ra) {
        try {
            roleService.retirerPermission(roleId, permissionId);
            ra.addFlashAttribute("success", "Permission retiree du role.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/roles";
    }
}