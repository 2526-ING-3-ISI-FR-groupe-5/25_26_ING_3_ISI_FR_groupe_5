package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Controller.AdminController;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Classe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.ClassesService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.InstitutSecurityService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
public class ClasseApiController {

    private final ClassesService classesService;
    private final InstitutSecurityService securityService;

    @GetMapping("/miennes")
    public List<Map<String, Object>> getMesClasses() {
        Long institutId = securityService.getInstitutIdCourant();

        List<Classe> classes;
        if (institutId == null) {
            classes = classesService.getAll();
        } else {
            classes = classesService.getByInstitut(institutId);
        }

        return classes.stream()
                .map(c -> Map.<String, Object>of(
                        "id", c.getId(),
                        "nom", c.getNom()
                ))
                .toList();
    }
}