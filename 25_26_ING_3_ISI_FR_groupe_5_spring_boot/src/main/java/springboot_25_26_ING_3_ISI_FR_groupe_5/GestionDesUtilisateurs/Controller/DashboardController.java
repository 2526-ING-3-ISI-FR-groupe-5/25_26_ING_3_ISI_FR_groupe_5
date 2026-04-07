package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Annee_academique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers.InscriptionMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers.SemestreMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.*;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final AnneeAcademiqueService anneeService;
    private final EtudiantService etudiantService;
    private final EnseignantService enseignantService;
    private final ClassesService classesService;
    private final UEService ueService;
    private final InscriptionService inscriptionService;
    private final InscriptionMapper inscriptionMapper;
    private final SemestreMapper semestreMapper;

    @GetMapping("/dashboard")
    public String dashboard(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model
    ) {
        try {
            Annee_academique anneeActive = anneeService.getAnneeActive();

            // Stats
            model.addAttribute("anneeActive", anneeActive);
            model.addAttribute("semestres",
                    semestreMapper.toResponseList(
                            anneeService.findById(anneeActive.getId())
                                    .getSemestres().stream().toList()
                    ));

            // Statistiques
            model.addAttribute("stats", new DashboardStats(
                    etudiantService.rechercher(
                            anneeActive.getId(), null,
                            PageRequest.of(0, 1)
                    ).getTotalElements(),
                    enseignantService.rechercher(
                            anneeActive.getId(), null,
                            PageRequest.of(0, 1)
                    ).getTotalElements(),
                    classesService.getByAnnee(
                            anneeActive.getId(), null,
                            PageRequest.of(0, 1)
                    ).getTotalElements(),
                    ueService.getByAnnee(anneeActive.getId()).size()
            ));

            // Dernières inscriptions
            model.addAttribute("dernieresInscriptions",
                    inscriptionMapper.toResponseList(
                            inscriptionService.getByClasseAndAnnee(null, anneeActive.getId())
                                    .stream().limit(10).toList()
                    ));

        } catch (Exception e) {
            // Pas d'année active encore
            model.addAttribute("anneeActive", null);
            model.addAttribute("stats", new DashboardStats(0, 0, 0, 0));
            model.addAttribute("dernieresInscriptions", java.util.List.of());
        }

        model.addAttribute("pageTitle", "Tableau de bord");
        model.addAttribute("currentPage", "dashboard");

        return "dashboard";
    }

    // Classe interne pour les stats
    public record DashboardStats(
            long totalEtudiants,
            long totalEnseignants,
            long totalClasses,
            long totalUes
    ) {}
}
