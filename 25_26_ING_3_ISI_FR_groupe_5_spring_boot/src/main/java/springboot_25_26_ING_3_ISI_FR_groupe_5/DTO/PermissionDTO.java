package springboot_25_26_ING_3_ISI_FR_groupe_5.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionDTO {
    private Long id;
    private String nom;
    private Boolean active;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dateCreation;

    // Liste des IDs des rôles pour éviter la récursivité infinie
    private Set<Long> roleIds = new HashSet<>();
}