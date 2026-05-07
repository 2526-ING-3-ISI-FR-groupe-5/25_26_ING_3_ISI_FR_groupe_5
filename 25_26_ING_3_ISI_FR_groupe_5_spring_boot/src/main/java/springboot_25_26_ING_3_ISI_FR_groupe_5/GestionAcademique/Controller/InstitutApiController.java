package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.DTO.institut.InstitutResponse;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Mappers.InstitutMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple.InstitutService;

import java.util.List;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Institut;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Config.Security;

/**
 * Contrôleur REST pour les instituts.
 * Utilisé principalement pour le sélecteur d'institut dans l'interface.
 */
@RestController
@RequestMapping("/api/instituts")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN_INSTITUT')")
public class InstitutApiController {

    private final InstitutService institutService;
    private final InstitutMapper institutMapper;

    /**
     * Récupère la liste de tous les instituts.
     * Pour SUPER_ADMIN : tous les instituts
     * Pour ADMIN_INSTITUT : uniquement son institut
     */
    @GetMapping("/list")
    public List<InstitutResponse> list() {
        return institutMapper.toResponseList(institutService.getAll());
    }

    /**
     * Récupère la liste des instituts pour un sélecteur (format simplifié).
     */
    @GetMapping("/select-options")
    public List<InstitutOption> getSelectOptions() {
        return institutService.getAll().stream()
                .map(inst -> new InstitutOption(inst.getId(), inst.getNom(), inst.getVille()))
                .toList();
    }

    /**
     * DTO simplifié pour les options de sélecteur.
     */
    public record InstitutOption(Long id, String nom, String ville) {}
}