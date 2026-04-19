package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", length = 3)
public abstract class Utilisateur implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    // ============================================
    // Attributs communs à TOUS les utilisateurs
    // ============================================

    @Column(nullable = false)
    protected String nom;

    @Column(nullable = false)
    protected String prenom;

    @Column(unique = true, nullable = false)
    protected String email;

    @Column(nullable = false)
    protected String password;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    protected LocalDate dateNaissance;

    @Column(nullable = false)
    protected String telephone;

    // ============================================
    // Gestion du compte
    // ============================================

    @Column(nullable = false)
    @Builder.Default
    protected boolean active = true;

    @Builder.Default
    protected boolean firstLogin = true;

    @Builder.Default
    protected boolean locked = false;

    @Builder.Default
    protected boolean expired = false;

    // ============================================
    // Localisation — commune à tous ✅
    // ============================================

    protected String adresse;
    protected String ville;
    protected String codePostal;
    protected String pays;

    // ============================================
    // Audit automatique via AuditingEntityListener
    // ============================================

    @CreatedDate
    @Column(updatable = false, nullable = false)
    protected LocalDateTime createdAt;

    @LastModifiedDate
    protected LocalDateTime updatedAt;

    // ✅ Annotations manquantes ajoutées
    @CreatedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", updatable = false)
    protected Utilisateur createdBy;

    @LastModifiedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    protected Utilisateur updatedBy;

    // ============================================
    // Seule relation commune — Spring Security
    // ============================================

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "utilisateurs_role",
            joinColumns = @JoinColumn(name = "utilisateur_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    protected Set<Role> roles = new HashSet<>();

    /*
     * ❌ SUPPRIMÉ — viole SRP, LSP, ISP
     * EnseignantInfo, EtudiantInfo, AssistantInfo, SurveillantInfo
     * n'ont pas leur place dans la classe mère.
     * Chaque classe fille porte ses propres attributs.
     */

    // ============================================
    // Méthodes utilitaires
    // ============================================

    public void addRole(Role role) {
        roles.add(role);
    }

    public boolean hasRole(String roleName) {
        return roles.stream()
                .anyMatch(r -> r.getNom().equals(roleName));
    }

    // ============================================
    // Implémentation UserDetails — Spring Security
    // ============================================

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (roles.isEmpty()) return Collections.emptyList();

        Set<GrantedAuthority> authorities = new HashSet<>();
        for (Role role : roles) {
            if (Boolean.TRUE.equals(role.getActive())) {
                authorities.add(
                        new SimpleGrantedAuthority("ROLE_" + role.getNom())
                );
                if (role.getPermissions() != null) {
                    for (Permission perm : role.getPermissions()) {
                        if (Boolean.TRUE.equals(perm.getActive())) {
                            authorities.add(
                                    new SimpleGrantedAuthority(perm.getNom())
                            );
                        }
                    }
                }
            }
        }
        return authorities;
    }

    @Override public boolean isEnabled() { return active; }
    @Override public boolean isAccountNonExpired() { return !expired; }
    @Override public boolean isAccountNonLocked() { return !locked; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public String getUsername() { return email; }
    @Override public String getPassword() { return password; }
}