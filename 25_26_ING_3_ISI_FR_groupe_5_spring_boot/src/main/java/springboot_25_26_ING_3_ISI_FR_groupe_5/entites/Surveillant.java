package springboot_25_26_ING_3_ISI_FR_groupe_5.entites;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("SRV")

public class Surveillant extends Utilisateur{
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private  Long id;
        private String nom;
        private String email;
        private  String telephone;
        @ManyToMany(fetch = FetchType.EAGER)
        private Collection<Appels> appels;
}
