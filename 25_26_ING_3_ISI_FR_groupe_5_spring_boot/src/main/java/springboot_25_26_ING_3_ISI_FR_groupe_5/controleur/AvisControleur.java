package springboot_25_26_ING_3_ISI_FR_groupe_5.controleur;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entites.Avis;
import springboot_25_26_ING_3_ISI_FR_groupe_5.service.AvisService;

@AllArgsConstructor
@RequestMapping("avis")
@RestController
public class AvisControleur {
    private final AvisService avisService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void creer(@RequestBody Avis avis){
        this.avisService.creer(avis);
    }
}
