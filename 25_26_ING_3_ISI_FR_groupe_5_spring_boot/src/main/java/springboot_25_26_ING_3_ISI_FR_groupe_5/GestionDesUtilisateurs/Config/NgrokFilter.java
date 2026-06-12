package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class NgrokFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        response.setHeader("ngrok-skip-browser-warning", "true");
        chain.doFilter(req, res);
    }
}