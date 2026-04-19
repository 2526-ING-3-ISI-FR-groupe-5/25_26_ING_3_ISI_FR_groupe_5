package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Appels;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.SeanceCours;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.UE;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.ValidationPresence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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
    @ManyToMany(mappedBy = "enseignant")
    private Collection<SeanceCours> seancecours =new ArrayList<>();

    @ManyToMany
    private Collection<UE> ues = new ArrayList<>();
    @ManyToMany(mappedBy = "enseignants", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<ProgrammationUE> programmations = new HashSet<>();
}
