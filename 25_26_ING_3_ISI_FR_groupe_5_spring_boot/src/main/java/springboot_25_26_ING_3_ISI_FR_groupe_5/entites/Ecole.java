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
public class Ecole {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;
    private  String nom;
    private  String adresse;
    private  String email;
    private  String telephone;
    @OneToMany(mappedBy = "ecoles")
    private Collection<Cycle> cycles;
    @OneToMany(mappedBy = "ecoles")
    private Collection<AssistantPedagogique> assistantPedagogiques;
}
