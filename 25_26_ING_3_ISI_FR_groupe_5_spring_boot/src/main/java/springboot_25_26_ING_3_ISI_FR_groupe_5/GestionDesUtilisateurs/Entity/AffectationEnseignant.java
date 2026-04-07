package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class AffectationEnseignant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Enseignant enseignant;

    @ManyToOne
    private ProgrammationUE programmationUE;
    private String gradeAuMomentL;
    private String statutAuMomentL;

    private Double tauxHoraire;
    private Long heuresAttribuees;
}