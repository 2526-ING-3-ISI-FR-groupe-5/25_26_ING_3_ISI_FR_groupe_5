package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Institut;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.UtilisateurRepository;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InstitutSecurityService {

    private final UtilisateurRepository utilisateurRepository;

    // ═══════════════════════════════════════════════════════════
    // RÉCUPÉRATION DE L'UTILISATEUR CONNECTÉ
    // ═══════════════════════════════════════════════════════════

    /**
     * Récupère l'utilisateur actuellement connecté
     */
    public Optional<Utilisateur> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof Utilisateur) {
            return Optional.of((Utilisateur) principal);
        }

        // Si le principal est une String (email), on cherche en base
        if (principal instanceof String email) {
            return utilisateurRepository.findByEmail(email);
        }

        return Optional.empty();
    }

    /**
     * Récupère l'utilisateur connecté ou lance une exception
     */
    public Utilisateur getCurrentUserOrThrow() {
        return getCurrentUser()
                .orElseThrow(() -> new AccessDeniedException("Utilisateur non authentifié"));
    }

    // ═══════════════════════════════════════════════════════════
    // VÉRIFICATION DES RÔLES
    // ═══════════════════════════════════════════════════════════

    /**
     * Vérifie si l'utilisateur a le rôle SUPER_ADMIN
     */
    public boolean isSuperAdmin(Utilisateur user) {
        return user.hasRole("SUPER_ADMIN");
    }

    /**
     * Vérifie si l'utilisateur a le rôle ADMIN_INSTITUT
     */
    public boolean isAdminInstitut(Utilisateur user) {
        return user.hasRole("ADMIN_INSTITUT");
    }

    /**
     * Vérifie si l'utilisateur courant est SUPER_ADMIN
     */
    public boolean isCurrentUserSuperAdmin() {
        return getCurrentUser()
                .map(this::isSuperAdmin)
                .orElse(false);
    }

    // ═══════════════════════════════════════════════════════════
    // RÉCUPÉRATION DE L'INSTITUT COURANT
    // ═══════════════════════════════════════════════════════════

    /**
     * Récupère l'ID de l'institut de l'utilisateur connecté
     * @return null pour SUPER_ADMIN, l'ID de l'institut pour les autres
     */
    public Long getInstitutIdCourant() {
        return getCurrentUser()
                .map(user -> {
                    if (isSuperAdmin(user)) {
                        return null; // Super Admin a accès à tout
                    }
                    Institut institut = user.getInstitut();
                    if (institut == null) {
                        throw new AccessDeniedException("Vous n'êtes rattaché à aucun institut");
                    }
                    return institut.getId();
                })
                .orElse(null);
    }

    /**
     * Récupère l'institut de l'utilisateur connecté
     * @return null pour SUPER_ADMIN, l'institut pour les autres
     */
    public Institut getInstitutCourant() {
        return getCurrentUser()
                .map(user -> {
                    if (isSuperAdmin(user)) {
                        return null;
                    }
                    Institut institut = user.getInstitut();
                    if (institut == null) {
                        throw new AccessDeniedException("Vous n'êtes rattaché à aucun institut");
                    }
                    return institut;
                })
                .orElse(null);
    }

    /**
     * Récupère l'ID de l'institut de l'utilisateur connecté (obligatoire)
     * @throws AccessDeniedException si SUPER_ADMIN ou pas d'institut
     */
    public Long getInstitutIdCourantObligatoire() {
        Long institutId = getInstitutIdCourant();
        if (institutId == null) {
            throw new AccessDeniedException("Veuillez sélectionner un institut");
        }
        return institutId;
    }

    // ═══════════════════════════════════════════════════════════
    // VÉRIFICATION DES DROITS D'ACCÈS
    // ═══════════════════════════════════════════════════════════

    /**
     * Vérifie si l'utilisateur courant peut accéder à un institut donné
     * @param institutId ID de l'institut à vérifier
     * @return true si SUPER_ADMIN ou si l'institut correspond
     */
    public boolean canAccessInstitut(Long institutId) {
        return getCurrentUser()
                .map(user -> canAccessInstitut(user, institutId))
                .orElse(false);
    }

    /**
     * Vérifie si un utilisateur peut accéder à un institut donné
     */
    public boolean canAccessInstitut(Utilisateur user, Long institutId) {
        if (institutId == null) {
            return false;
        }
        // Super Admin peut tout voir
        if (isSuperAdmin(user)) {
            return true;
        }
        // Les autres doivent appartenir à l'institut
        Institut userInstitut = user.getInstitut();
        return userInstitut != null && userInstitut.getId().equals(institutId);
    }

    /**
     * Vérifie l'accès à un institut et lance une exception si refusé
     */
    public void checkAccessInstitut(Long institutId) {
        if (!canAccessInstitut(institutId)) {
            throw new AccessDeniedException("Vous n'avez pas accès à cet institut");
        }
    }

    /**
     * Vérifie l'accès à un institut pour un utilisateur donné
     */
    public void checkAccessInstitut(Utilisateur user, Long institutId) {
        if (!canAccessInstitut(user, institutId)) {
            throw new AccessDeniedException("Vous n'avez pas accès à cet institut");
        }
    }

    // ═══════════════════════════════════════════════════════════
    // VÉRIFICATION DES DROITS DE GESTION (ÉCRITURE/SUPPRESSION)
    // ═══════════════════════════════════════════════════════════

    /**
     * Vérifie si l'utilisateur courant peut gérer (modifier/supprimer) un institut
     * SUPER_ADMIN : oui sur tout
     * ADMIN_INSTITUT : oui uniquement sur son institut
     * Autres : non
     */
    public boolean canManageInstitut(Long institutId) {
        return getCurrentUser()
                .map(user -> canManageInstitut(user, institutId))
                .orElse(false);
    }

    /**
     * Vérifie si un utilisateur peut gérer un institut
     */
    public boolean canManageInstitut(Utilisateur user, Long institutId) {
        if (institutId == null) {
            return false;
        }
        // Super Admin peut tout gérer
        if (isSuperAdmin(user)) {
            return true;
        }
        // Admin Institut peut gérer SON institut uniquement
        if (isAdminInstitut(user)) {
            Institut userInstitut = user.getInstitut();
            return userInstitut != null && userInstitut.getId().equals(institutId);
        }
        return false;
    }

    /**
     * Vérifie les droits de gestion et lance une exception si refusé
     */
    public void checkManageInstitut(Long institutId) {
        if (!canManageInstitut(institutId)) {
            throw new AccessDeniedException("Vous n'avez pas les droits pour gérer cet institut");
        }
    }

    /**
     * Vérifie les droits de gestion pour un utilisateur donné
     */
    public void checkManageInstitut(Utilisateur user, Long institutId) {
        if (!canManageInstitut(user, institutId)) {
            throw new AccessDeniedException("Vous n'avez pas les droits pour gérer cet institut");
        }
    }

    // ═══════════════════════════════════════════════════════════
    // RÉSOLUTION DE L'INSTITUT À UTILISER
    // ═══════════════════════════════════════════════════════════

    /**
     * Résout l'institut à utiliser en fonction du rôle de l'utilisateur
     * @param user L'utilisateur
     * @param requestedInstitutId L'institut demandé (peut être null)
     * @return L'ID de l'institut à utiliser (null pour tous les instituts)
     * @throws AccessDeniedException si l'utilisateur n'a pas les droits
     */
    public Long resolveInstitutId(Utilisateur user, Long requestedInstitutId) {
        // SUPER_ADMIN : peut choisir n'importe quel institut ou null pour tous
        if (isSuperAdmin(user)) {
            return requestedInstitutId;
        }

        // ADMIN_INSTITUT et autres : forcé à leur institut
        Institut userInstitut = user.getInstitut();
        if (userInstitut == null) {
            throw new AccessDeniedException("Vous n'êtes rattaché à aucun institut");
        }

        // Si un institut est demandé, vérifier qu'il correspond
        if (requestedInstitutId != null && !requestedInstitutId.equals(userInstitut.getId())) {
            throw new AccessDeniedException("Vous ne pouvez pas agir sur un autre institut");
        }

        return userInstitut.getId();
    }

    /**
     * Résout l'institut pour l'utilisateur courant
     */
    public Long resolveInstitutId(Long requestedInstitutId) {
        Utilisateur user = getCurrentUserOrThrow();
        return resolveInstitutId(user, requestedInstitutId);
    }

    // ═══════════════════════════════════════════════════════════
    // MÉTHODES UTILITAIRES POUR LES FILTRES JPA
    // ═══════════════════════════════════════════════════════════

    /**
     * Retourne l'ID de l'institut pour les requêtes filtrées
     * - null pour SUPER_ADMIN (pas de filtre)
     * - l'ID de l'institut pour les autres
     */
    public Long getInstitutIdForQuery() {
        return getInstitutIdCourant();
    }

    /**
     * Vérifie si un filtre par institut doit être appliqué
     */
    public boolean shouldFilterByInstitut() {
        return !isCurrentUserSuperAdmin();
    }

    // ═══════════════════════════════════════════════════════════
    // MÉTHODES POUR THYMELEAF / VUES
    // ═══════════════════════════════════════════════════════════

    /**
     * Retourne le nom de l'institut courant pour affichage
     */
    public String getCurrentInstitutName() {
        return getCurrentUser()
                .map(user -> {
                    if (isSuperAdmin(user)) {
                        return "Tous les instituts";
                    }
                    Institut institut = user.getInstitut();
                    return institut != null ? institut.getNom() : "Aucun institut";
                })
                .orElse("Non connecté");
    }

    /**
     * Vérifie si le sélecteur d'institut doit être affiché
     */
    public boolean shouldShowInstitutSelector() {
        return isCurrentUserSuperAdmin();
    }
}