package springboot_25_26_ING_3_ISI_FR_groupe_5.Entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;

import java.util.Collection;
@Getter
@Setter
@Entity
@DiscriminatorValue("PRT")
public class Parent extends Utilisateur {
    private  String fonction;

    @OneToMany(mappedBy ="parent")
    private Collection <Etudiant> etudiants;
}
