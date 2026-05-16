package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.UE;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.ValidationPresence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Specialite;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.ProgrammationUE;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.TypeEnseignant;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("ENS")

public class  Enseignant extends Utilisateur {

    private String grade;
    private String specialite;
    private String typeEnseignant;
    @OneToMany(mappedBy = "enseignant")
    private Collection<ValidationPresence> validationPresences;


    @ManyToMany
    private Collection<UE> ues = new ArrayList<>();
    @ManyToMany(mappedBy = "enseignants", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<ProgrammationUE> programmations = new HashSet<>();
}

