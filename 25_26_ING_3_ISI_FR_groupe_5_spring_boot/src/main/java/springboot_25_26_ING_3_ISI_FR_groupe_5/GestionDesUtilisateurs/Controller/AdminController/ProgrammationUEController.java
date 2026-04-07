package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Controller.AdminController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Annee_academique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.programmation.ProgrammationRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.semestre.SemestreResponse;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.ProgrammationUE;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/programmations")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ProgrammationUEController {

    private final ProgrammationUEService programmationService;
    private final ProgrammationUEMapper programmationMapper;
    private final UEService ueService;
    private final UEMapper ueMapper;
    private final SemestreService semestreService;
    private final SemestreMapper semestreMapper;
    private final ClassesService classesService;
    private final ClassesMapper classesMapper;
    private final EnseignantService enseignantService;
    private final EnseignantMapper enseignantMapper;
    private final AnneeAcademiqueService anneeService;

    @GetMapping
    public String liste(
            @RequestParam(required = false) Long classeId,
            @RequestParam(required = false) Long anneeId,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        Annee_academique annee = null;
        try {
            annee = anneeId != null
                    ? anneeService.findById(anneeId)
                    : anneeService.getAnneeActive();
        } catch (Exception e) {
            model.addAttribute("anneeActive", null);
        }

        if (annee != null) {
            model.addAttribute("anneeActive", annee);

            // ✅ Semestres de l'année active uniquement
            List<SemestreResponse> semestres = semestreMapper
                    .toResponseList(semestreService.getByAnnee(annee.getId()));
            model.addAttribute("semestres", semestres);

            // ✅ Semestre actif pré-sélectionné
            try {
                model.addAttribute("semestreActif",
                        semestreMapper.toResponse(
                                semestreService.getSemestreActif(annee.getId())
                        ));
            } catch (Exception e) {
                model.addAttribute("semestreActif", null);
            }

            // Programmations filtrées
            List<ProgrammationUE> programmations = classeId != null
                    ? programmationService.getByClasseAndAnnee(classeId, annee.getId())
                    : new ArrayList<>();

            model.addAttribute("programmations",
                    programmationMapper.toResponseList(programmations));
        }

        model.addAttribute("annees", anneeService.getAll());
        model.addAttribute("classes",
                classesMapper.toResponseList(
                        classesService.getByAnnee(
                                annee != null ? annee.getId() : null,
                                null, PageRequest.of(0, 100)
                        ).getContent()
                ));
        model.addAttribute("ues",
                ueMapper.toResponseList(
                        ueService.getAll()
                ));
        model.addAttribute("enseignants",
                enseignantMapper.toResponseList(enseignantService.getAll()));
        model.addAttribute("classeIdSelectionne", classeId);
        model.addAttribute("pageTitle", "Programmation des UE");
        model.addAttribute("currentPage", "programmations");

        return "programmations/liste";
    }

    @GetMapping("/creer")
    public String formulaireCreer(
            @RequestParam(required = false) Long classeId,
            Model model
    ) {
        Annee_academique anneeActive = anneeService.getAnneeActive();

        model.addAttribute("form", new ProgrammationRequest());
        model.addAttribute("ues",
                ueMapper.toResponseList(ueService.getByAnnee(anneeActive.getId())));
        model.addAttribute("semestres",
                semestreMapper.toResponseList(
                        semestreService.getByAnnee(anneeActive.getId())));
        model.addAttribute("classes",
                classesMapper.toResponseList(
                        classesService.getByAnnee(
                                anneeActive.getId(), null, PageRequest.of(0, 100)
                        ).getContent()));
        model.addAttribute("enseignants",
                enseignantMapper.toResponseList(
                        enseignantService.rechercher(
                                anneeActive.getId(), null, PageRequest.of(0, 100)
                        ).getContent()));
        model.addAttribute("classeIdSelectionne", classeId);
        return "programmations/form";
    }

    @PostMapping("/creer")
    public String creer(
            @Valid @ModelAttribute("form") ProgrammationRequest request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            Annee_academique anneeActive = anneeService.getAnneeActive();
            model.addAttribute("ues",
                    ueMapper.toResponseList(ueService.getByAnnee(anneeActive.getId())));
            model.addAttribute("semestres",
                    semestreMapper.toResponseList(
                            semestreService.getByAnnee(anneeActive.getId())));
            model.addAttribute("classes",
                    classesMapper.toResponseList(
                            classesService.getByAnnee(
                                    anneeActive.getId(), null, PageRequest.of(0, 100)
                            ).getContent()));
            model.addAttribute("enseignants",
                    enseignantMapper.toResponseList(
                            enseignantService.rechercher(
                                    anneeActive.getId(), null, PageRequest.of(0, 100)
                            ).getContent()));
            return "programmations/form";
        }

        try {
            programmationService.programmer(
                    request.getUeId(),
                    request.getSemestreId(),
                    request.getClasseId(),
                    request.getDheure(),
                    request.getNbrCredit(),
                    request.getEnseignantIds()
            );
            redirectAttributes.addFlashAttribute("succes",
                    "UE programmée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }

        return "redirect:/programmations?classeId=" + request.getClasseId();
    }

    @PostMapping("/{id}/modifier")
    public String modifier(
            @PathVariable Long id,
            @RequestParam Long dheure,
            @RequestParam Long nbrCredit,
            @RequestParam Set<Long> enseignantIds,
            @RequestParam(required = false) String libelle,           // ✅ Ajout du libellé FR
            @RequestParam(required = false) String libelleAnglais,    // ✅ Ajout du libellé EN
            RedirectAttributes redirectAttributes
    ) {
        try {
            // ✅ On passe les nouveaux paramètres au service
            ProgrammationUE p = programmationService.modifier(
                    id, dheure, nbrCredit, enseignantIds, libelle, libelleAnglais
            );
            redirectAttributes.addFlashAttribute("succes",
                    "Programmation modifiée avec succès");
            return "redirect:/programmations?classeId=" + p.getClasse().getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
            return "redirect:/programmations";
        }
    }
    @PostMapping("/{id}/supprimer")
    public String supprimer(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            ProgrammationUE p = programmationService.findById(id);
            Long classeId = p.getClasse().getId();
            programmationService.supprimer(id);
            redirectAttributes.addFlashAttribute("succes",
                    "Programmation supprimée avec succès");
            return "redirect:/programmations?classeId=" + classeId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
            return "redirect:/programmations";
        }
    }

    // Ajoute cette méthode dans ProgrammationUEController
    @GetMapping("/annee/{anneeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public String getByAnnee(@PathVariable Long anneeId, Model model) {
        Annee_academique annee = anneeService.findById(anneeId);
        List<ProgrammationUE> programmations = programmationService.getByAnnee(anneeId);

        model.addAttribute("programmations", programmationMapper.toResponseList(programmations));
        model.addAttribute("anneeActive", annee);
        return "programmations/liste";
    }
}
