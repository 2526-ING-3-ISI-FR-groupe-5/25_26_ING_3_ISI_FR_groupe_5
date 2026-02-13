package springboot_25_26_ING_3_ISI_FR_groupe_5.Entites;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.aspectj.bridge.IMessage;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Enums.TypeSexe;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance
@DiscriminatorColumn(name="Type", length = 3)
//Administrateur=ADM, AssPeda= ASP, Enseignant=ENS, Etudiant=ETD, Parent=PRT, Surveillant=SRV

public abstract class Utilisateur  implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    protected Long id;

    @NotBlank(message = "Le nom ne peut pas être vide.")
    @Size(min = 1, max = 100, message = "Le nom doit contenir entre 1 et 100 caractères.")
    protected String nom;
    @NotBlank(message = "Le nom ne peut pas être vide.")
    @Size(min = 1, max = 100, message = "Le nom doit contenir entre 1 et 100 caractères.")
    protected String prenom;
    protected String adresse;

    @Column(unique = true)
    @NotBlank(message = "L'email ne peut pas être vide.")
    @Email(message = "L'email doit être un format valide.")
    @Size(max = 255, message = "L'email ne peut pas dépasser 255 caractères.")
    protected String email;

    protected String telephone;

    @NotBlank(message = "Le mot de passe ne peut pas être vide.")
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères.")
    private String password;


    @Enumerated(EnumType.STRING)
    @NotBlank(message = "Le sexe ne peut pas être nul.")
    protected TypeSexe sexe;

    protected Boolean active;
    private boolean firstLogin = true;

    @NotNull(message = "La date de création ne peut pas être nulle.")
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotBlank(message=" La Date est requise est sous la forme  yyyy-MM-dd ")
    protected Date dateCreation;
    @OneToOne
    private Localisation localisation;
    @ManyToMany(mappedBy = "utilisateur")
    private Set<Role> roles= new HashSet<>();
    @ManyToMany(mappedBy = "utilisateurs")
    private  Collection<Institut> institutCollection= new ArrayList<>();

    @OneToMany(mappedBy = "utilisateur")
    private Collection<Localisation> localisations;
    @ManyToMany
    private Collection<Administrateur> admin=new ArrayList<>();


   private boolean enabled = false;
   private boolean accountLocked = false;



   private String otpCode;
   private LocalDateTime otpExpiration;

   private int loginAttempts = 0;
   private LocalDateTime lastLogin;



   @Override
   public Collection<? extends GrantedAuthority> getAuthorities() {
    Set<GrantedAuthority> authorities = new HashSet<>();

    for (Role role : roles) {
     if (role.getActive()) {
      authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getNom()));
     }

     for (Permission perm : role.getPermissions()) {
      if (perm.getActive()) {
       authorities.add(new SimpleGrantedAuthority(perm.getNom()));
      }
     }
    }
    return authorities;
   }

   @Override
   public String getUsername() {
    return email;
   }

   @Override
   public boolean isAccountNonExpired() {
    return true;
   }

   @Override
   public boolean isAccountNonLocked() {
    return !accountLocked;
   }

   @Override
   public boolean isCredentialsNonExpired() {
    return true;
   }

   @Override
   public boolean isEnabled() {
    return enabled;
   }
}
