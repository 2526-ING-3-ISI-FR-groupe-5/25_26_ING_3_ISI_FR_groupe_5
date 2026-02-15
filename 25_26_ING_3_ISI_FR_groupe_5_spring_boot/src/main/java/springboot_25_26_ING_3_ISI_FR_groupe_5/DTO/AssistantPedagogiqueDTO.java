package springboot_25_26_ING_3_ISI_FR_groupe_5.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class AssistantPedagogiqueDTO extends  UtilisateurDTO{
    private Long ecoleId;
    private Collection<Long> appelsIds; // IDs des appels
    private Collection<Long> justificatifsIds; // IDs des justificatifs
}
