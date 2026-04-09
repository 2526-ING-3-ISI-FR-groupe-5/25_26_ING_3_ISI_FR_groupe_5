package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.*;

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
@DiscriminatorColumn(name = "Type", length = 3)
public abstract class Utilisateur implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

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

    @Column(nullable = false)
    @Builder.Default
    protected boolean active = true;

    protected boolean firstLogin = true;
    protected boolean locked = false;
    protected boolean expired = false;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    protected LocalDateTime createdAt;

    @LastModifiedDate
    protected LocalDateTime updatedAt;


    @ManyToMany
    @JoinTable(name = "utilisateur_ecole")
    @Builder.Default
    private Collection<Ecole> ecoles = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "utilisateur_institut")
    @Builder.Default
    private Collection<Institut> instituts = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "utilisateur_filiere")
    @Builder.Default
    private Collection<Filiere> filieres = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "utilisateur_cycle")
    @Builder.Default
    private Collection<Cycle> cycles = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "utilisateur_specialite")
    @Builder.Default
    private Collection<Specialite> specialites = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "utilisateur_utilisateur")
    @Builder.Default
    private Collection<Utilisateur> utilisateursGeres = new ArrayList<>();

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL)
    @Builder.Default
    private Collection<Appels> appels = new ArrayList<>();





    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "utilisateurs_role")
    protected Set<Role> roles = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (roles.isEmpty()) return Collections.emptyList();
        Set<GrantedAuthority> authorities = new HashSet<>();
        for (Role role : roles) {
            if (Boolean.TRUE.equals(role.getActive())) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getNom()));
            }
            for (Permission perm : role.getPermissions()) {
                if (Boolean.TRUE.equals(perm.getActive())) {
                    authorities.add(new SimpleGrantedAuthority(perm.getNom()));
                }
            }
        }
        return authorities;
    }

    @Override
    public boolean isEnabled() { return active; }
    @Override
    public boolean isAccountNonExpired() { return !expired; }
    @Override
    public boolean isAccountNonLocked() { return !locked; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public String getUsername() { return email; }
    @Override
    public String getPassword() { return password; }
}