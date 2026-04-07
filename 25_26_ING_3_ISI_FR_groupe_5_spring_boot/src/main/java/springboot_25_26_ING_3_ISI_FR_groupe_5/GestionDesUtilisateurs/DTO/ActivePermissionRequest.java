package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActivePermissionRequest {
    private  Long id;
    private Boolean active;
}
