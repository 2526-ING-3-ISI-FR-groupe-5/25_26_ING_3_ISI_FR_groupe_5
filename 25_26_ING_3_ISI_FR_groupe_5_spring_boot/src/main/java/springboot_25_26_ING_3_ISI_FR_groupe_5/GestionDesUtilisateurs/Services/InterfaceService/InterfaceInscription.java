package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService;

import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Inscription;

public interface InterfaceInscription {
    @Transactional
    Inscription inscrire(Long etudiantId, Long classeId, Long anneeId);
}
