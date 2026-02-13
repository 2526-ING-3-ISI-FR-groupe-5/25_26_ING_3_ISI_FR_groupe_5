package springboot_25_26_ING_3_ISI_FR_groupe_5.Entites;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity

public class UE {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private  String nom;
    private String code;
    private Long nb_heure;
    private Long  credit;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateCreation;
    //***** Arevoir
    @ManyToOne
    private Justificatif justificatif;
}
