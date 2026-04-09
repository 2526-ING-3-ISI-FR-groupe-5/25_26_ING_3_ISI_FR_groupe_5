package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.assistant;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssistantResponse {

    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String fonction;
    private boolean active;
    private List<String> classesNoms= new ArrayList<>();
}
