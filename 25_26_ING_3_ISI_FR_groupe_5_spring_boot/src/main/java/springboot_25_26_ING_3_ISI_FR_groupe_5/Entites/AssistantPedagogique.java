package springboot_25_26_ING_3_ISI_FR_groupe_5.Entites;

import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("ASP")

public class AssistantPedagogique extends Utilisateur {

    @ManyToOne
    private Ecole ecoles;

    @OneToMany(mappedBy = "assistantPedagogique")
    private Collection<Appels> appels;
    @OneToMany(mappedBy = "assistantPedagogique")
    private Collection<Justificatif> justificatif;

    @Override
    public boolean isPresent() {
        return false;
    }
}
