package springboot_25_26_ING_3_ISI_FR_groupe_5.entites;

import jakarta.persistence.*;
import jdk.jfr.DataAmount;
import lombok.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.enums.TypeSexe;

import java.util.Collection;
import java.util.Date;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance
@DiscriminatorColumn(name="Type", length = 3)
//Administrateur=ADM, AssPeda= ASP, Enseignant=ENS, Etudiant=ETD, Parent=PRT, Surveillant=SRV

public abstract class Utilisateur {
   @Id
   @GeneratedValue(strategy = GenerationType.TABLE)
    protected Long id;
    protected String nom;
    @Column(unique = true)
    protected String email;
    protected String telephone;
    protected   String motDePasse;
    @Enumerated(EnumType.STRING)
    protected TypeSexe sexe;
    protected Boolean active;
    protected Date dateCreation;
    @OneToOne
    private Localisation localisation;
    @ManyToMany(mappedBy = "utilisateurs")
    private Collection<Role> roles;
}
