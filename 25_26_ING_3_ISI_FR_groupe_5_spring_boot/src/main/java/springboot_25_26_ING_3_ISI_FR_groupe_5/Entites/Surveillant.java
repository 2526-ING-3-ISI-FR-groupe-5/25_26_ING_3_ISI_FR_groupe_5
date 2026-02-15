package springboot_25_26_ING_3_ISI_FR_groupe_5.Entites;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("SRV")

public class Surveillant extends Utilisateur{

        @ManyToMany(mappedBy = "surveillant")
        private Collection<Appels> appels= new ArrayList<>();

    @Override
    public boolean isPresent() {
        return false;
    }
}
