package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Ecole;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Filiere;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Institut;

import java.util.ArrayList;
import java.util.Collection;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("ADM")
public class Administrateur extends Utilisateur {

 private String fonction;

 // ✅ Relation avec Ecole (côté propriétaire)
 @ManyToMany
 @JoinTable(
         name = "administrateur_ecole",
         joinColumns = @JoinColumn(name = "administrateur_id"),
         inverseJoinColumns = @JoinColumn(name = "ecole_id")
 )
 @Builder.Default
 private Collection<Ecole> ecoles = new ArrayList<>();

 // ✅ Relation avec Institut (côté propriétaire)
 @ManyToMany
 @JoinTable(
         name = "administrateur_institut",
         joinColumns = @JoinColumn(name = "administrateur_id"),
         inverseJoinColumns = @JoinColumn(name = "institut_id")
 )
 @Builder.Default
 private Collection<Institut> instituts = new ArrayList<>();

 // ✅ Relation avec Filiere (côté propriétaire)
 @ManyToMany
 @JoinTable(
         name = "administrateur_filiere",
         joinColumns = @JoinColumn(name = "administrateur_id"),
         inverseJoinColumns = @JoinColumn(name = "filiere_id")
 )
 @Builder.Default
 private Collection<Filiere> filieres = new ArrayList<>();

 // ✅ Relation avec Utilisateur (côté propriétaire)
 @ManyToMany
 @JoinTable(
         name = "administrateur_utilisateur",
         joinColumns = @JoinColumn(name = "administrateur_id"),
         inverseJoinColumns = @JoinColumn(name = "utilisateur_id")
 )
 @Builder.Default
 private Collection<Utilisateur> utilisateurs = new ArrayList<>();
}