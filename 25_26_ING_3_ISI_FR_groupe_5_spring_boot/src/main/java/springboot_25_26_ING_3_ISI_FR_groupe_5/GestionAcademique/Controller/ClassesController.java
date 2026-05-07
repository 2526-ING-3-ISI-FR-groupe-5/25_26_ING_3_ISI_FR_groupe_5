package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Annee_academique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.Appels;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Classe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Niveau;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.DTO.classes.ClassesRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Inscription;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.ProgrammationUE;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.SessionAppel;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Mappers.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple.*;

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
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Config.Security;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Etudiant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers.InscriptionMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.InscriptionService;

@Controller
@RequestMapping("/enseignant/classes")
@PreAuthorize("hasAnyRole('ENSEIGNANT', 'ETUDIANT', 'ASSISTANT')")
@RequiredArgsConstructor
public class ClassesController {

    private final ClassesService classesService;
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

    @GetMapping
    public String liste(
            @RequestParam(required = false) Long specialiteId,
            @RequestParam(required = false) Long niveauId,
            @RequestParam(required = false) Long anneeId,
            Model model
    ) {
        Annee_academique annee = null;
        try {
            annee = anneeId != null
                    ? anneeService.findById(anneeId)
                    : anneeService.getAnneeActive();
        } catch (Exception e) {
            // Pas d'année active
        }

        List<Classe> classesList = new ArrayList<>();

        if (niveauId != null && niveauId > 0) {
            classesList = classesService.getByNiveau(niveauId);
        } else if (specialiteId != null && specialiteId > 0) {
            List<Niveau> niveaux = niveauService.getBySpecialite(specialiteId);
            for (Niveau niveau : niveaux) {
                classesList.addAll(classesService.getByNiveau(niveau.getId()));
            }
        } else {
            classesList = classesService.getAll();
        }

        List<Niveau> tousNiveaux = niveauService.getAll();

        model.addAttribute("classes", classesMapper.toResponseList(classesList));
        model.addAttribute("niveaux", niveauMapper.toResponseList(tousNiveaux));
        model.addAttribute("specialites", specialiteMapper.toResponseList(specialiteService.getAll()));
        model.addAttribute("annees", anneeService.getAll());
        model.addAttribute("anneeActive", annee);
        model.addAttribute("specialiteIdSelectionne", specialiteId);
        model.addAttribute("niveauIdSelectionne", niveauId);
        model.addAttribute("form", new ClassesRequest());

        return "classe/liste";
    }

    @GetMapping("/{id}")
    public String detail(
            @PathVariable Long id,
            @RequestParam(required = false) Long anneeId,
            Model model
    ) {
        Annee_academique annee = anneeId != null
                ? anneeService.findById(anneeId)
                : anneeService.getAnneeActive();

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

    @PostMapping("/creer")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN_INSTITUT')")
    public String creer(
            @Valid @ModelAttribute("form") ClassesRequest request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            model.addAttribute("specialites", specialiteMapper.toResponseList(specialiteService.getAll()));
            model.addAttribute("niveaux", niveauMapper.toResponseList(niveauService.getAll()));
            return "classe/liste";
        }

        try {
            classesService.creer(request.getNom(), request.getNiveauId());
            redirectAttributes.addFlashAttribute("succes", "Classe créée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }

        return "redirect:/enseignant/classes";
    }

    @GetMapping("/{id}/modifier")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN_INSTITUT')")
    public String formulaireModifier(@PathVariable Long id, Model model) {
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

    @PostMapping("/{id}/modifier")
    public String modifier(
            @PathVariable Long id,
            @Valid @ModelAttribute("form") ClassesRequest request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            model.addAttribute("classe", classesMapper.toResponse(classesService.findById(id)));
            model.addAttribute("specialites", specialiteMapper.toResponseList(specialiteService.getAll()));
            model.addAttribute("niveaux", niveauMapper.toResponseList(niveauService.getAll()));
            return "classe/modifier";
        }

        try {
            classesService.modifier(id, request.getNom(), request.getNiveauId());
            redirectAttributes.addFlashAttribute("succes", "Classe modifiée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }

        return "redirect:/enseignant/classes/" + id;
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            classesService.supprimer(id);
            redirectAttributes.addFlashAttribute("succes", "Classe supprimée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/enseignant/classes";
    }

    @GetMapping("/{id}/json")
    @ResponseBody
    public ClassesRequest getClasseJson(@PathVariable Long id) {
        Classe classe = classesService.findById(id);
        ClassesRequest request = new ClassesRequest();
        request.setNom(classe.getNom());
        request.setNiveauId(classe.getNiveau().getId());
        return request;
    }
}