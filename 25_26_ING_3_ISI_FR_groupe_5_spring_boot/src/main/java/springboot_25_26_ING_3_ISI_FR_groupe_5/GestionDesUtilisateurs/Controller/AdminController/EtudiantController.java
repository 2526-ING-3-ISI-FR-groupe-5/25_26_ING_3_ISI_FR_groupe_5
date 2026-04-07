package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Controller.AdminController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Annee_academique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Etudiant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.etudiant.EtudiantRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Inscription;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers.ClassesMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers.EtudiantMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers.InscriptionMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.AnneeAcademiqueService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.ClassesService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.EtudiantService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.InscriptionService;

import java.util.List;

@Controller
@RequestMapping("/etudiants")
@RequiredArgsConstructor
public class EtudiantController {

    private final EtudiantService etudiantService;
    private final EtudiantMapper etudiantMapper;
    private final InscriptionService inscriptionService;
    private final InscriptionMapper inscriptionMapper;
    private final ClassesService classesService;
    private final ClassesMapper classesMapper;
    private final AnneeAcademiqueService anneeService;

    // ══════════════════════════════════════════
    // LISTE avec filtre + année
    // ══════════════════════════════════════════
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSISTANT')")
    public String liste(
            @RequestParam(required = false) String recherche,
            @RequestParam(required = false) Long anneeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        // Utiliser l'année active si non précisée
        Annee_academique annee = anneeId != null
                ? anneeService.findById(anneeId)
                : anneeService.getAnneeActive();

        Page<Etudiant> etudiants = etudiantService.rechercher(
                annee.getId(), recherche, PageRequest.of(page, size)
        );

        model.addAttribute("etudiants",
                etudiantMapper.toResponseList(etudiants.getContent()));
        model.addAttribute("anneeActive", annee);
        model.addAttribute("annees", anneeService.getAll());
        model.addAttribute("recherche", recherche);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", etudiants.getTotalPages());
        model.addAttribute("totalElements", etudiants.getTotalElements());

        return "etudiants/liste";
    }

    // ══════════════════════════════════════════
    // FORMULAIRE CRÉATION
    // ══════════════════════════════════════════
    @GetMapping("/nouveau")
    @PreAuthorize("hasRole('ADMIN')")
    public String formulaireCreer(Model model) {
        model.addAttribute("form", new EtudiantRequest());
        model.addAttribute("classes",
                classesMapper.toResponseList(classesService.getByAnnee(
                        anneeService.getAnneeActive().getId(), null,
                        PageRequest.of(0, 100)
                ).getContent()));
        return "etudiants/form";
    }

    // ══════════════════════════════════════════
    // CRÉER
    // ══════════════════════════════════════════
    @PostMapping("/creer")
    @PreAuthorize("hasRole('ADMIN')")
    public String creer(
            @Valid @ModelAttribute("form") EtudiantRequest request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            model.addAttribute("classes",
                    classesMapper.toResponseList(
                            classesService.getByAnnee(
                                    anneeService.getAnneeActive().getId(),
                                    null, PageRequest.of(0, 100)
                            ).getContent()));
            return "etudiants/form";
        }

        try {
            Etudiant etudiant = etudiantMapper.toEntity(request);
            etudiantService.creer(etudiant, request.getClasseId());
            redirectAttributes.addFlashAttribute("succes",
                    "Étudiant créé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }

        return "redirect:/etudiants";
    }

    // ══════════════════════════════════════════
    // DÉTAIL
    // ══════════════════════════════════════════
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSISTANT')")
    public String detail(@PathVariable Long id, Model model) {
        Etudiant etudiant = etudiantService.findById(id);
        List<Inscription> historique = inscriptionService
                .getHistoriqueEtudiant(id);

        model.addAttribute("etudiant",
                etudiantMapper.toResponse(etudiant));
        model.addAttribute("historique",
                inscriptionMapper.toResponseList(historique));

        return "etudiants/detail";
    }

    // ══════════════════════════════════════════
    // FORMULAIRE MODIFICATION
    // ══════════════════════════════════════════
    @GetMapping("/{id}/modifier")
    @PreAuthorize("hasRole('ADMIN')")
    public String formulaireModifier(@PathVariable Long id, Model model) {
        Etudiant etudiant = etudiantService.findById(id);
        EtudiantRequest form = new EtudiantRequest();
        form.setNom(etudiant.getNom());
        form.setPrenom(etudiant.getPrenom());
        form.setEmail(etudiant.getEmail());
        form.setTelephone(etudiant.getTelephone());
        form.setDateNaissance(etudiant.getDateNaissance());

        model.addAttribute("etudiant", etudiantMapper.toResponse(etudiant));
        model.addAttribute("form", form);
        return "etudiants/modifier";
    }

    // ══════════════════════════════════════════
    // MODIFIER
    // ══════════════════════════════════════════
    @PostMapping("/{id}/modifier")
    @PreAuthorize("hasRole('ADMIN')")
    public String modifier(
            @PathVariable Long id,
            @Valid @ModelAttribute("form") EtudiantRequest request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            model.addAttribute("etudiant",
                    etudiantMapper.toResponse(etudiantService.findById(id)));
            return "etudiants/modifier";
        }

        try {
            Etudiant data = etudiantMapper.toEntity(request);
            etudiantService.modifier(id, data);
            redirectAttributes.addFlashAttribute("succes",
                    "Étudiant modifié avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }

        return "redirect:/etudiants/" + id;
    }

    // ══════════════════════════════════════════
    // ACTIVER / DÉSACTIVER
    // ══════════════════════════════════════════
    @PostMapping("/{id}/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    public String toggle(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            etudiantService.toggleActif(id);
            redirectAttributes.addFlashAttribute("succes",
                    "Statut modifié avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/etudiants/" + id;
    }

    // ══════════════════════════════════════════
    // RÉINITIALISER MOT DE PASSE
    // ══════════════════════════════════════════
    @PostMapping("/{id}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    public String resetPassword(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            etudiantService.reinitialiserMotDePasse(id);
            redirectAttributes.addFlashAttribute("succes",
                    "Mot de passe réinitialisé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/etudiants/" + id;
    }
}