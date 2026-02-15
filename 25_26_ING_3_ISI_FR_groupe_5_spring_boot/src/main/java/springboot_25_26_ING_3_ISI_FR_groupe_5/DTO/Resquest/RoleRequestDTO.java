package springboot_25_26_ING_3_ISI_FR_groupe_5.DTO.Resquest;

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
public class RoleRequestDTO {
    private Long id;
    private TypeRole nom;
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dateCreation;

    private Boolean active;

    private List<Long> permissionIds = new ArrayList<>();

    private List<Long> utilisateurIds = new ArrayList<>();
}