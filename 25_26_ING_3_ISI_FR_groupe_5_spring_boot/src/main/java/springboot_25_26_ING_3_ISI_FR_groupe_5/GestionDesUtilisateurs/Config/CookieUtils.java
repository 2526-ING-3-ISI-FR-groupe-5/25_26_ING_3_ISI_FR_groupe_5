package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Config;



import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import jakarta.servlet.http.HttpServletResponse;

public class CookieUtils {

    // ══════════════════════════════════════════
    // EXTRAIRE un cookie par nom
    // ══════════════════════════════════════════
    public static String extractCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals(name)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    // ══════════════════════════════════════════
    // AJOUTER un cookie
    // ══════════════════════════════════════════
    public static void addCookie(HttpServletResponse response,
                                 String name,
                                 String value,
                                 int maxAgeSeconds) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // true en production (HTTPS)
        cookie.setPath("/");
        cookie.setMaxAge(maxAgeSeconds);
        response.addCookie(cookie);
    }

    // ══════════════════════════════════════════
    // SUPPRIMER un cookie
    // ══════════════════════════════════════════
    public static void deleteCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}

