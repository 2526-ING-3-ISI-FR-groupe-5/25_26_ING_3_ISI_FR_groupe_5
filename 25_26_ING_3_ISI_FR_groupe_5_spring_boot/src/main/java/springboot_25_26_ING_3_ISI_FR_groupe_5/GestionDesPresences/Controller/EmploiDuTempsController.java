package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Annee_academique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Mappers.AnneeAcademiqueMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Mappers.ClassesMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers.EnseignantMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Mappers.UEMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple.AnneeAcademiqueService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple.ClassesService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.EnseignantService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple.UEService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Classe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Config.Security;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;

@Controller
@RequestMapping("/emploisDeTemps")
@RequiredArgsConstructor
public class EmploiDuTempsController {

    private final AnneeAcademiqueService anneeService;
    private final AnneeAcademiqueMapper anneeMapper;
    private final ClassesService classesService;
    private final ClassesMapper classesMapper;
    private final UEService ueService;
    private final UEMapper ueMapper;
    private final EnseignantService enseignantService;
    private final EnseignantMapper enseignantMapper;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSISTANT_PEDAGOGIQUE')")
    public String afficher(
            @RequestParam(required = false) Long classeId,
            Model model
    ) {
        // 1. Année académique active
        Annee_academique anneeActive = null;
        try {
            anneeActive = anneeService.getAnneeActive();
        } catch (Exception ignored) {}

        // 2. Toutes les classes
        var classes = classesService.getAll();

        // 3. UEs filtrées par classe si une classe est sélectionnée, sinon toutes
        var ues = (classeId != null)
                ? ueService.getByClasse(classeId)
                : ueService.getAll();

        // 4. Tous les enseignants (déjà filtrés par rôle ENSEIGNANT via la table de discrimination)
        var enseignants = enseignantService.getAll();

        model.addAttribute("anneeActive", anneeActive);
        model.addAttribute("annees", anneeMapper.toResponseList(anneeService.getAll()));
        model.addAttribute("classes", classesMapper.toResponseList(classes));
        model.addAttribute("ues", ueMapper.toResponseList(ues));
        model.addAttribute("enseignants", enseignantMapper.toResponseList(enseignants));
        model.addAttribute("classeIdSelectionne", classeId);

        return "emploisDeTemps/emploisDeTemps";
    }
}
