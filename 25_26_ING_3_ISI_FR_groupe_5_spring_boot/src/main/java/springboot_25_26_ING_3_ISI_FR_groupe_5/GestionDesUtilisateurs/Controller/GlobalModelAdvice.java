package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple.InstitutSecurityService;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAdvice {

    private final InstitutSecurityService securityService;

    @ModelAttribute("currentInstitutName")
    public String currentInstitutName(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "";
        }
        try {
            return securityService.getCurrentInstitutName();
        } catch (Exception e) {
            return "";
        }
    }
}
