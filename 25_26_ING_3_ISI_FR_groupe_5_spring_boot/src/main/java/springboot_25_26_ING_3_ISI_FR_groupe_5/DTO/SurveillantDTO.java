package springboot_25_26_ING_3_ISI_FR_groupe_5.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class SurveillantDTO extends UtilisateurDTO{
private List<Long> appelIds;
}
