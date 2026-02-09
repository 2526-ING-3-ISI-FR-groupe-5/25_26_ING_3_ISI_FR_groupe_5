package springboot_25_26_ING_3_ISI_FR_groupe_5.entites;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Institut {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;
    private  String nom;
    private  String ville;
    private  String adresse;
    private  String email;
    private  String telephone;
    private  String localite;
    @OneToMany(mappedBy = "institut")
    private Collection<Ecole> ecoles;
}
