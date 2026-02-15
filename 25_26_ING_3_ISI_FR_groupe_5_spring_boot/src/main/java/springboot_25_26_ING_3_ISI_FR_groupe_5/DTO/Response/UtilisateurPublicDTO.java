package springboot_25_26_ING_3_ISI_FR_groupe_5.DTO.Response;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Enums.TypeSexe;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UtilisateurPublicDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private TypeSexe sexe;

    private String type;
}
