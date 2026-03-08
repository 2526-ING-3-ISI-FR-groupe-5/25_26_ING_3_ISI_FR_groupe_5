package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity;

import jakarta.persistence.*;
import lombok.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Appels;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Ecole;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Justificatif;

import java.util.Collection;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("ASP")

public class AssistantPedagogique extends Utilisateur {
  private String fonction;
    @ManyToOne
    private Ecole ecoles;
    @OneToMany(mappedBy = "assistantPedagogique")
    private Collection<Appels> appels;
    @OneToMany(mappedBy = "assistantPedagogique")
    private Collection<Justificatif> justificatif;
}
