package springboot_25_26_ING_3_ISI_FR_groupe_5.entites;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity

public class Localisation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;
    private  Double Longitude ;
    private  Double Latitude;
    private Date date;
    @OneToOne
    private Utilisateur utilisateur;
}
