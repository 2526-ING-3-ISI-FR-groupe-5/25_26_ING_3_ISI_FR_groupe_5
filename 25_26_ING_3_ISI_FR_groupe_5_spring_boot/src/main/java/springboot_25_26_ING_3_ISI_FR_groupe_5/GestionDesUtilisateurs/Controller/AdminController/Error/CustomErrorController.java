package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Controller.AdminController.Error;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @GetMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object errorMessage = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            // Ajouter le message d'erreur au modèle
            if (errorMessage != null && !errorMessage.toString().isEmpty()) {
                model.addAttribute("error", errorMessage.toString());
            }

            switch (statusCode) {
                case 401:
                    return "error/401";
                case 403:
                    return "error/403";
                case 404:
                    return "error/404";
                case 500:
                    return "error/500";
                default:
                    return "error/default";
            }
        }

        return "error/default";
    }

    // Page personnalisée pour "Non trouvé" (404)
    @GetMapping("/notFound")
    public String notFound() {
        return "error/404";
    }

    // Page personnalisée pour "Accès interdit" (403)
    @GetMapping("/accessDenied")
    public String accessDenied() {
        return "error/403";
    }

    // Page personnalisée pour "Session expirée"
    @GetMapping("/sessionExpired")
    public String sessionExpired() {
        return "error/401";
    }
}