package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.inscription;

import CarnetRouge.CarnetRouge.GDU.Enum.StatutInscription;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InscriptionStatutRequest {

    @NotNull(message = "Le statut est obligatoire")
    private StatutInscription statut;
}