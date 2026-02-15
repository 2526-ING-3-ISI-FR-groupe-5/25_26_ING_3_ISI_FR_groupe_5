package springboot_25_26_ING_3_ISI_FR_groupe_5.controleur;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entites.Utilisateur;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Repository.UtilisateurRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.service.UtilisateurService;


@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
public class UtilisateurControleur {

    private UtilisateurService utilisateurService;

    @PostMapping(path = "inscription")
    public void inscription(@RequestBody Utilisateur utilisateur) {
        log.info("Inscription");
        this.utilisateurService.inscription(utilisateur);
    }
}
