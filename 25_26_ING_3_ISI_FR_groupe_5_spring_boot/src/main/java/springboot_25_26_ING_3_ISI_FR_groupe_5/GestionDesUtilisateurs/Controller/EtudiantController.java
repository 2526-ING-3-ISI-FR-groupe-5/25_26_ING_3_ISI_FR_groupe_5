package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Annee_academique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.Appels;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.SessionAppel;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Mappers.AppelsMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Repository.AppelsRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Services.ServiceImple.AppelsService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Services.ServiceImple.SessionAppelService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Etudiant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.etudiant.EtudiantRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Inscription;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Mappers.ClassesMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers.EtudiantMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers.InscriptionMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple.AnneeAcademiqueService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple.ClassesService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.EtudiantService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.InscriptionService;

import java.util.List;

@Controller
@RequestMapping("/etudiants")
@PreAuthorize("hasAnyRole('ENSEIGNANT',  'ADMIN_INSTITUT')")

@RequiredArgsConstructor
public class EtudiantController {

    private final EtudiantService etudiantService;
    private final EtudiantMapper etudiantMapper;
    private final InscriptionService inscriptionService;
    private final InscriptionMapper inscriptionMapper;
    private final ClassesService classesService;
    private final ClassesMapper classesMapper;
    private final AnneeAcademiqueService anneeService;
private final SessionAppelService sessionAppelService;
private final AppelsService appelsService;
private final AppelsRepository appelsRepository;
private final AppelsMapper appelsMapper;
    // ══════════════════════════════════════════
    // LISTE avec filtre + année
    // ══════════════════════════════════════════
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ASSISTANT')")
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
        // Needed for the "Ajouter étudiant" modal
        model.addAttribute("classes",
                classesMapper.toResponseList(classesService.getByAnnee(
                        annee.getId(), null, PageRequest.of(0, 200)
                ).getContent()));

        return "etudiants/liste";
    }

    // ══════════════════════════════════════════
    // FORMULAIRE CRÉATION
    // ══════════════════════════════════════════
    @GetMapping("/nouveau")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
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
    @PreAuthorize("hasRole('SUPER_ADMIN')")
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
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ASSISTANT')")
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

    // ════════════════��═════════════════════════
    // FORMULAIRE MODIFICATION
    // ══════════════════════════════════════════
    @GetMapping("/{id}/modifier")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
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
    @PreAuthorize("hasRole('SUPER_ADMIN')")
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
    @PreAuthorize("hasRole('SUPER_ADMIN')")
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
    @PreAuthorize("hasRole('SUPER_ADMIN')")
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

    @GetMapping("/mon-espace")
    public String dashboardEtudiant(Model model, @AuthenticationPrincipal Utilisateur utilisateur) {
        Long etudiantId = ((Etudiant) utilisateur).getId();
        Long classeId = ((Etudiant) utilisateur).getClasse().getId();

        // 1. Chercher la session active pour sa classe
        SessionAppel session = sessionAppelService.getSessionActivePourClasse(classeId);
        model.addAttribute("sessionActive", session);

        // 2. Statistiques (Tu peux ajouter des méthodes dans ton AppelsService)
        model.addAttribute("nbAbsencesNJ", appelsRepository.countAbsencesNonJustifieesByEtudiant(etudiantId));
        model.addAttribute("nbRetards", appelsService.getRetardsByEtudiant(etudiantId).size());
        // ... calcul taux présence ...

        // 3. Historique récent
        List<Appels> historique = appelsService.getByEtudiant(etudiantId);
        model.addAttribute("appels", appelsMapper.toResponseList(historique));

        return "etudiants/dashboard";
    }
}