package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exceptions;

import jakarta.validation.constraints.NotNull;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String cycleNotFound) {
        super(cycleNotFound+": " );
    }

    public ResourceNotFoundException(String école, String id, @NotNull(message = "L'école est obligatoire") Long ecoleId) {
    }
}
