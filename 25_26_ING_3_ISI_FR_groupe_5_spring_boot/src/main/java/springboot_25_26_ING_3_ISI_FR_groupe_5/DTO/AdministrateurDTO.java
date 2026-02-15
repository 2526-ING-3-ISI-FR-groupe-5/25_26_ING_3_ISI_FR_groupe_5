package springboot_25_26_ING_3_ISI_FR_groupe_5.DTO;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
public class AdministrateurDTO extends UtilisateurDTO{
    private List<Long> ecoleIds;
    private List<Long> institutIds;
    private List<Long> filiereIds;

}

