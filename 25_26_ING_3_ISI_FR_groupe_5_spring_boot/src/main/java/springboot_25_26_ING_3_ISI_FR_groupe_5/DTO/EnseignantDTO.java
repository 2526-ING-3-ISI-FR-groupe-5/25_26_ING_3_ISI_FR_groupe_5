package springboot_25_26_ING_3_ISI_FR_groupe_5.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class EnseignantDTO extends  UtilisateurDTO{
    private String grade;
    private String specialite;
    private List<Long> validationPresenceIds = new ArrayList<>();
    private List<Long> seanceCoursIds = new ArrayList<>();
    private List<Long> appelIds = new ArrayList<>();
}
