package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {

    private final RefreshTokenService refreshTokenService;

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) {

        // ✅ Supprimer le refresh token de la base
        if (request.getCookies() != null) {
            Arrays.stream(request.getCookies())
                    .filter(c -> "REFRESH_TOKEN".equals(c.getName()))
                    .findFirst()
                    .ifPresent(c -> {
                        try {
                            refreshTokenService.deleteByToken(c.getValue());
                            log.info("Refresh token supprimé");
                        } catch (Exception e) {
                            log.warn("Erreur suppression refresh token : {}", e.getMessage());
                        }
                    });
        }
    }
}