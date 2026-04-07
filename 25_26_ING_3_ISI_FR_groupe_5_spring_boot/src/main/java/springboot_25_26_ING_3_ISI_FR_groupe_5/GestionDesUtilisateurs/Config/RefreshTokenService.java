package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.RefreshToken;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.RefreshTokenRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.UtilisateurRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    // Durée de validité du refresh token : 7 jours
    private static final long REFRESH_TOKEN_EXPIRY_DAYS = 7;

    private final RefreshTokenRepository refreshTokenRepo;
    private final UtilisateurRepository utilisateurRepo;

    // ══════════════════════════════════════════
    // CRÉER un refresh token pour un utilisateur
    // ══════════════════════════════════════════
    @Transactional
    public RefreshToken creer(String email) {
        Utilisateur utilisateur = utilisateurRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException(
                        "Utilisateur introuvable : " + email
                ));

        // Supprimer l'ancien refresh token s'il existe
        refreshTokenRepo.deleteByUtilisateur(utilisateur);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .utilisateur(utilisateur)
                .expiryDate(LocalDateTime.now().plusDays(REFRESH_TOKEN_EXPIRY_DAYS))
                .revoked(false)
                .build();

        return refreshTokenRepo.save(refreshToken);
    }

    // ══════════════════════════════════════════
    // VÉRIFIER un refresh token
    // ══════════════════════════════════════════
    public RefreshToken verifier(String token) {
        RefreshToken refreshToken = refreshTokenRepo.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Refresh token introuvable"));

        if (refreshToken.isExpired()) {
            refreshTokenRepo.delete(refreshToken);
            throw new RuntimeException("Refresh token expiré — veuillez vous reconnecter");
        }

        if (refreshToken.isRevoked()) {
            throw new RuntimeException("Refresh token révoqué — veuillez vous reconnecter");
        }

        return refreshToken;
    }

    // ══════════════════════════════════════════
    // SUPPRIMER par token (logout)
    // ══════════════════════════════════════════
    @Transactional
    public void deleteByToken(String token) {
        refreshTokenRepo.deleteByToken(token);
    }

    // ══════════════════════════════════════════
    // SUPPRIMER tous les tokens d'un utilisateur
    // ══════════════════════════════════════════
    @Transactional
    public void deleteByUtilisateur(String email) {
        Utilisateur utilisateur = utilisateurRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException(
                        "Utilisateur introuvable : " + email
                ));
        refreshTokenRepo.deleteByUtilisateur(utilisateur);
    }

    // ══════════════════════════════════════════
    // RÉVOQUER un token (sans le supprimer)
    // ══════════════════════════════════════════
    @Transactional
    public void revoquer(String token) {
        refreshTokenRepo.findByToken(token).ifPresent(rt -> {
            rt.setRevoked(true);
            refreshTokenRepo.save(rt);
        });
    }
}
