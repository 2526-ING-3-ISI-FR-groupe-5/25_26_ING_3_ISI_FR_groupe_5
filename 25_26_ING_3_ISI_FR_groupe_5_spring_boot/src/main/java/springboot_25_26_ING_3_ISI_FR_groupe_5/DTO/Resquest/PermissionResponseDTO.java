package springboot_25_26_ING_3_ISI_FR_groupe_5.DTO.Resquest;

import lombok.*;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PermissionResponseDTO {
    private Long id;
    private String nom;
    private Boolean active;
    private Date dateCreation;
    private Set<String> roles; // Ou Set<Long> si tu préfères renvoyer les IDs des rôles
}
