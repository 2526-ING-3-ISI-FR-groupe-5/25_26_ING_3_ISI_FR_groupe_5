package springboot_25_26_ING_3_ISI_FR_groupe_5.DTO;

import lombok.Getter;
import lombok.Setter;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Enums.TypeNiveau;

import java.util.Date;
@Getter
@Setter
public class  EtudiantDTO extends  UtilisateurDTO{
    private String matricule;
    private TypeNiveau niveau;
    private Date dateNaissance;
    private Long parentId;
    private Long filiereId;

}
