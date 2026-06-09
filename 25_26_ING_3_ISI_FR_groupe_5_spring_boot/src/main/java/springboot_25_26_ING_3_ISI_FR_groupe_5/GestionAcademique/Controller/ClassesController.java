package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Annee_academique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Classe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Niveau;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.DTO.classes.ClassesRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Semestre;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Repository.ClassesRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Inscription;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.SessionAppel;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Etudiant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.AssistantPedagogique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;

import java.util.ArrayList;
import java.util.List;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Mappers.ClassesMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Mappers.NiveauMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Mappers.SpecialiteMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple.AnneeAcademiqueService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple.ClassesService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple.NiveauService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple.SpecialiteService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Mappers.ProgrammationUEMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Mappers.SessionAppelMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Services.ServiceImple.AppelsService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Services.ServiceImple.ProgrammationUEService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Services.ServiceImple.SessionAppelService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers.InscriptionMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.InscriptionService;

@Controller
@RequestMapping("/classes")
@RequiredArgsConstructor
public class ClassesController {

    private final ClassesService classesService;
    private final ClassesRepository classesRepo;
    private final ClassesMapper classesMapper;
    private final SpecialiteService specialiteService;
    private final SpecialiteMapper specialiteMapper;
    private final NiveauService niveauService;
    private final NiveauMapper niveauMapper;
    private final AppelsService appelsService;
    private final SessionAppelService sessionAppelService;
    private final SessionAppelMapper sessionAppelMapper;
    private final AnneeAcademiqueService anneeService;
    private final InscriptionService inscriptionService;
    private final InscriptionMapper inscriptionMapper;
    private final ProgrammationUEService programmationService;
    private final ProgrammationUEMapper programmationMapper;

    // ══════════════════════════════════════════
    // LISTE — Avec pagination
    // ══════════════════════════════════════════

    @GetMapping
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ETUDIANT', 'ASSISTANT', 'SUPER_ADMIN', 'ADMIN_INSTITUT')")
    public String liste(
            @RequestParam(required = false) Long specialiteId,
            @RequestParam(required = false) Long niveauId,
            @RequestParam(required = false) Long anneeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model,
            RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal Utilisateur user) {

        Annee_academique annee = null;
        try {
            annee = anneeId != null
                    ? anneeService.findById(anneeId)
                    : anneeService.getAnneeActive();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur",
                    "Aucune annee academique active.");
            return "redirect:/admin/annees";
        }

        if (annee == null) {
            redirectAttributes.addFlashAttribute("erreur",
                    "Aucune annee academique disponible.");
            return "redirect:/admin/annees";
        }

        List<Classe> classesList = getClassesForUser(user, specialiteId, niveauId);

        int totalClasses = classesList.size();
        int totalPages = (int) Math.ceil((double) totalClasses / size);
        int start = page * size;
        int end = Math.min(start + size, totalClasses);
        List<Classe> pageContent = start < totalClasses ? classesList.subList(start, end) : List.of();

        List<Niveau> tousNiveaux = niveauService.getAll();

        model.addAttribute("classes", classesMapper.toResponseList(pageContent));
        model.addAttribute("niveaux", niveauMapper.toResponseList(tousNiveaux));
        model.addAttribute("specialites", specialiteMapper.toResponseList(specialiteService.getAll()));
        model.addAttribute("annees", anneeService.getAll());
        model.addAttribute("anneeActive", annee);
        model.addAttribute("specialiteIdSelectionne", specialiteId);
        model.addAttribute("niveauIdSelectionne", niveauId);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalClasses", totalClasses);
        model.addAttribute("form", new ClassesRequest());

        return "classe/liste";
    }

    // ══════════════════════════════════════════
    // DÉTAIL
    // ══════════════════════════════════════════

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ETUDIANT', 'ASSISTANT', 'SUPER_ADMIN', 'ADMIN_INSTITUT')")
    public String detail(
            @PathVariable Long id,
            @RequestParam(required = false) Long anneeId,
            Model model,
            RedirectAttributes redirectAttributes) {

        Annee_academique annee = null;
        try {
            annee = anneeId != null
                    ? anneeService.findById(anneeId)
                    : anneeService.getAnneeActive();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur",
                    "Aucune annee academique active. Veuillez en activer une depuis les parametres");
            return "redirect:/classes";
        }

        if (annee == null) {
            redirectAttributes.addFlashAttribute("erreur",
                    "Aucune annee academique disponible");
            return "redirect:/classes";
        }

        Classe classe = classesService.findById(id);
        List<Inscription> inscriptions = inscriptionService.getByClasseAndAnnee(id, annee.getId());
        SessionAppel sessionActive = sessionAppelService.getSessionActivePourClasse(id);

        model.addAttribute("classe", classesMapper.toResponse(classe));
        model.addAttribute("inscriptions", inscriptionMapper.toResponseList(inscriptions));
        model.addAttribute("totalEtudiants", inscriptions.size());
        model.addAttribute("sessionActive", sessionActive != null ? sessionAppelMapper.toResponse(sessionActive) : null);
        model.addAttribute("nbPresents", 0);
        model.addAttribute("nbAbsents", 0);

        return "classe/detail";
    }

    // ══════════════════════════════════════════
    // CRÉER — Assistant, Admin uniquement
    // ══════════════════════════════════════════

    @PostMapping("/creer")
    @PreAuthorize("hasAnyRole('ASSISTANT', 'ADMIN_INSTITUT', 'SUPER_ADMIN')")
    public String creer(
            @Valid @ModelAttribute("form") ClassesRequest request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("specialites", specialiteMapper.toResponseList(specialiteService.getAll()));
            model.addAttribute("niveaux", niveauMapper.toResponseList(niveauService.getAll()));
            return "classe/liste";
        }

        try {
            classesService.creer(request.getNom(), request.getNiveauId());
            redirectAttributes.addFlashAttribute("succes", "Classe creee avec succes");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }

        return "redirect:/classes";
    }

    // ══════════════════════════════════════════
    // FORMULAIRE MODIFIER — Assistant, Admin uniquement
    // ══════════════════════════════════════════

    @GetMapping("/{id}/modifier")
    @PreAuthorize("hasAnyRole('ASSISTANT', 'ADMIN_INSTITUT', 'SUPER_ADMIN')")
    public String formulaireModifier(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            Annee_academique annee = anneeService.getAnneeActive();
            model.addAttribute("anneeActive", annee);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur",
                    "Aucune annee academique active. Veuillez en activer une depuis les parametres");
            return "redirect:/classes";
        }

        Classe classe = classesService.findById(id);
        ClassesRequest form = new ClassesRequest();
        form.setNom(classe.getNom());
        form.setNiveauId(classe.getNiveau().getId());

        model.addAttribute("classe", classesMapper.toResponse(classe));
        model.addAttribute("specialites", specialiteMapper.toResponseList(specialiteService.getAll()));
        model.addAttribute("niveaux", niveauMapper.toResponseList(niveauService.getAll()));
        model.addAttribute("form", form);

        return "classe/modifier";
    }

    // ══════════════════════════════════════════
    // MODIFIER — Assistant, Admin uniquement
    // ══════════════════════════════════════════

    @PostMapping("/{id}/modifier")
    @PreAuthorize("hasAnyRole('ASSISTANT', 'ADMIN_INSTITUT', 'SUPER_ADMIN')")
    public String modifier(
            @PathVariable Long id,
            @Valid @ModelAttribute("form") ClassesRequest request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("classe", classesMapper.toResponse(classesService.findById(id)));
            model.addAttribute("specialites", specialiteMapper.toResponseList(specialiteService.getAll()));
            model.addAttribute("niveaux", niveauMapper.toResponseList(niveauService.getAll()));
            return "classe/modifier";
        }

        try {
            classesService.modifier(id, request.getNom(), request.getNiveauId());
            redirectAttributes.addFlashAttribute("succes", "Classe modifiee avec succes");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }

        return "redirect:/classes/" + id;
    }

    // ══════════════════════════════════════════
    // SUPPRIMER — Assistant, Admin uniquement
    // ══════════════════════════════════════════

    @PostMapping("/{id}/supprimer")
    @PreAuthorize("hasAnyRole('ASSISTANT', 'ADMIN_INSTITUT', 'SUPER_ADMIN')")
    public String supprimer(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        try {
            classesService.supprimer(id);
            redirectAttributes.addFlashAttribute("succes", "Classe supprimee avec succes");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/classes";
    }

    // ══════════════════════════════════════════
    // JSON — API
    // ══════════════════════════════════════════

    @GetMapping("/{id}/json")
    @ResponseBody
    public ClassesRequest getClasseJson(@PathVariable Long id) {
        Classe classe = classesService.findById(id);
        ClassesRequest request = new ClassesRequest();
        request.setNom(classe.getNom());
        request.setNiveauId(classe.getNiveau().getId());
        return request;
    }

    // ══════════════════════════════════════════
    // PRIVÉ — Filtrage par rôle
    // ══════════════════════════════════════════

    private List<Classe> getClassesForUser(Utilisateur user, Long specialiteId, Long niveauId) {
        List<Classe> classesList;

        if (user instanceof Enseignant enseignant) {
            Long institutId = user.getInstitut() != null ? user.getInstitut().getId() : null;
            Semestre semestreActif = (institutId != null)
                    ? anneeService.getSemestreActif(institutId)
                    : null;

            if (semestreActif != null) {
                classesList = classesRepo.findByEnseignantIdAndSemestreId(
                        enseignant.getId(), semestreActif.getId());
            } else {
                classesList = List.of();
            }

        } else if (user instanceof Etudiant etudiant) {
            Classe classe = etudiant.getClasse();
            classesList = classe != null ? List.of(classe) : List.of();

        } else if (user instanceof AssistantPedagogique assistant) {
            classesList = new ArrayList<>(assistant.getClasses());
            if (classesList.isEmpty() && user.getInstitut() != null) {
                classesList = classesService.getByInstitut(user.getInstitut().getId());
            }

        } else {
            classesList = classesService.getAll();
        }

        if (niveauId != null && niveauId > 0) {
            classesList = classesList.stream()
                    .filter(c -> c.getNiveau() != null && c.getNiveau().getId().equals(niveauId))
                    .toList();
        }
        if (specialiteId != null && specialiteId > 0) {
            classesList = classesList.stream()
                    .filter(c -> c.getSpecialite() != null && c.getSpecialite().getId().equals(specialiteId))
                    .toList();
        }

        return classesList;
    }
}