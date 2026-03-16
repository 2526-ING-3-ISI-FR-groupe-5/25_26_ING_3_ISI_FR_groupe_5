package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity;

import jakarta.persistence.*;
import lombok.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Appels;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("SRV")

public class Surveillant extends Utilisateur {
        private String fonction;

        @ManyToMany(mappedBy = "surveillant")
        private Collection<Appels> appels= new ArrayList<>();
}
