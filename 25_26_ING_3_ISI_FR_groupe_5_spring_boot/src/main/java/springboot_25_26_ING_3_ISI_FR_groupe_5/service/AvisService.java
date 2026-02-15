package springboot_25_26_ING_3_ISI_FR_groupe_5.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entites.Avis;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Repository.AvisRepository;

@AllArgsConstructor
@Service
public class AvisService {
    private final AvisRepository avisRepository;
    public void creer(Avis avis){
        this.avisRepository.save(avis);
    }
}
