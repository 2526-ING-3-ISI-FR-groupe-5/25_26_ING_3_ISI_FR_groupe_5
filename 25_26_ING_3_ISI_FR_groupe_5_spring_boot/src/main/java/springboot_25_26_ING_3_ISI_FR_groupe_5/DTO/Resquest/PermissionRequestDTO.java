package springboot_25_26_ING_3_ISI_FR_groupe_5.DTO.Resquest;

import lombok.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PermissionRequestDTO {
    private String nom;
    private Boolean active;
    private Date dateCreation;
}
