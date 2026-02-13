package springboot_25_26_ING_3_ISI_FR_groupe_5.DTO;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdministrateurDTO extends UtilisateurDTO{
    private  Long id;
    private String nom;
    private String email;
    private  String telephone;
}

