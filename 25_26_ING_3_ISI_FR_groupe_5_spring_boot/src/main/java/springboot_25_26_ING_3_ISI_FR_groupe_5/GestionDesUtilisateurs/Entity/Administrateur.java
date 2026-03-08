package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity;
import jakarta.persistence.*;
import lombok.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Ecole;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Filiere;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Institut;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("ADM")
public class Administrateur extends Utilisateur {
   private  String fonction;

    @ManyToMany(mappedBy = "administrateurs")
    private Collection<Ecole> ecoles= new ArrayList<>();
    @ManyToMany(mappedBy = "administrateur")
    private  Collection<Institut> instituts= new ArrayList<>();
    @ManyToMany(mappedBy = "administrateurs")
    private  Collection<Filiere> filieres= new ArrayList<>();
    @ManyToMany(mappedBy = "admin")
    private  Collection<Utilisateur> utilisateurs= new ArrayList<>();

}
