package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Controller.AdminController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Annee_academique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Migration.MigrationRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Migration.MigrationResponse;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Inscription;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers.AnneeAcademiqueMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers.InscriptionMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.AnneeAcademiqueService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.InscriptionService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.MigrationResultat;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.MigrationService;

import java.util.List;

@Controller
@RequestMapping("/migration")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class MigrationController {

    private final MigrationService migrationService;
    private final AnneeAcademiqueService anneeService;
    private final AnneeAcademiqueMapper anneeMapper;
    private final InscriptionService inscriptionService;
    private final InscriptionMapper inscriptionMapper;

    // ══════════════════════════════════════════
    // PAGE PRINCIPALE
    // ══════════════════════════════════════════
    @GetMapping
    public String index(Model model) {
        Annee_academique anneeActive = anneeService.getAnneeActive();

        // Statistiques avant migration
        List<Inscription> inscriptions = inscriptionService
                .getByClasseAndAnnee(null, anneeActive.getId());

        long admis = inscriptions.stream()
                .filter(i -> "ADMIS".equals(i.getDecisionFinAnnee()))
                .count();
        long redoublants = inscriptions.stream()
                .filter(i -> "REDOUBLANT".equals(i.getDecisionFinAnnee()))
                .count();
        long exclus = inscriptions.stream()
                .filter(i -> "EXCLU".equals(i.getDecisionFinAnnee()))
                .count();
        long sansDecision = inscriptions.stream()
                .filter(i -> i.getDecisionFinAnnee() == null)
                .count();

        model.addAttribute("anneeActive", anneeActive);
        model.addAttribute("annees",
                anneeMapper.toResponseList(anneeService.getAll()));
        model.addAttribute("form", new MigrationRequest());
        model.addAttribute("totalInscriptions", inscriptions.size());
        model.addAttribute("admis", admis);
        model.addAttribute("redoublants", redoublants);
        model.addAttribute("exclus", exclus);
        model.addAttribute("sansDecision", sansDecision);

        return "migration/index";
    }

    // ══════════════════════════════════════════
    // LANCER LA MIGRATION
    // ══════════════════════════════════════════
    @PostMapping("/lancer")
    public String lancer(
            @Valid @ModelAttribute("form") MigrationRequest request,
            BindingResult result,
            RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("erreur",
                    "Veuillez sélectionner une année cible");
            return "redirect:/migration";
        }

        try {
            MigrationResultat resultat = migrationService
                    .migrer(request.getNouvelleAnneeId());

            // Construire la réponse
            MigrationResponse response = MigrationResponse.builder()
                    .totalTraite(resultat.getTotal())
                    .admis(resultat.getAdmis())
                    .redoublants(resultat.getRedoublants())
                    .exclus(resultat.getExclus())
                    .diplomes(resultat.getDiplomes())
                    .ignores(resultat.getIgnores())
                    .message(resultat.toString())
                    .build();

            redirectAttributes.addFlashAttribute("resultat", response);
            redirectAttributes.addFlashAttribute("succes",
                    "Migration effectuée avec succès !");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }

        return "redirect:/migration";
    }

    // ══════════════════════════════════════════
    // DÉCISION FIN D'ANNÉE (par étudiant)
    // ══════════════════════════════════════════
    @PostMapping("/decision/{inscriptionId}")
    public String enregistrerDecision(
            @PathVariable Long inscriptionId,
            @RequestParam String decision,
            @RequestParam(required = false) Long classeId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            inscriptionService.enregistrerDecision(inscriptionId, decision);
            redirectAttributes.addFlashAttribute("succes",
                    "Décision enregistrée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erreur", e.getMessage());
        }

        return classeId != null
                ? "redirect:/classes/" + classeId + "/inscriptions"
                : "redirect:/migration";
    }
}

