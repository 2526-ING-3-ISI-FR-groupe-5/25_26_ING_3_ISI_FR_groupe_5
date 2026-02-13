package springboot_25_26_ING_3_ISI_FR_groupe_5.entites;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
@Getter
@Setter
@Entity
@DiscriminatorValue("PRT")
public class Parent extends Utilisateur{
    @OneToMany(mappedBy ="parent")
    private Collection <Etudiant> etudiants;
}
