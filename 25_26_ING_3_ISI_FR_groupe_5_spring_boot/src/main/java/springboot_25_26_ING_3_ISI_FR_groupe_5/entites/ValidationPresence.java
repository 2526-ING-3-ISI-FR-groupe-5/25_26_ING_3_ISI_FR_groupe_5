package springboot_25_26_ING_3_ISI_FR_groupe_5.entites;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
@Getter
@Setter
@Builder

@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ValidationPresence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String qrCode;
    private  String codePin;
    private Date dateDeCreation;
    @ManyToOne
    private Enseignant enseignant;
}
