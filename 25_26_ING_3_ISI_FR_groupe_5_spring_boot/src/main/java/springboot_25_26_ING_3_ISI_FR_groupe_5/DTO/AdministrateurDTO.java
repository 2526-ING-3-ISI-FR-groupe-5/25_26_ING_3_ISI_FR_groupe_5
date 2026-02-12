package springboot_25_26_ING_3_ISI_FR_groupe_5.DTO;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("ADM")
public class AdministrateurDTO extends UtilisateurDTO{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;
    private String nom;
    private String email;
    private  String telephone;
}

