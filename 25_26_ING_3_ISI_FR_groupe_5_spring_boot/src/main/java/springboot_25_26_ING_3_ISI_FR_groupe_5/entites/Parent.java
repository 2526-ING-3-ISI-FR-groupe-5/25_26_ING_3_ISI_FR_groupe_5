package springboot_25_26_ING_3_ISI_FR_groupe_5.entites;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import java.util.Collection;

@Entity
public class Parent extends Utilisateur{
    @OneToMany(mappedBy ="parent")
    private Collection <Etudiant> etudiants;
}
