package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Resquest;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Enums.TypeRole;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleUpdateDescriptionRequestDTO {
    private String description;
}