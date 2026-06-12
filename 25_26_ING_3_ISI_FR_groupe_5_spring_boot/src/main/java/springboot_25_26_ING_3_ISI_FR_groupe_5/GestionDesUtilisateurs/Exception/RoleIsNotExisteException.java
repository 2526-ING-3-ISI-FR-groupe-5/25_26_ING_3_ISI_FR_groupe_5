package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exception;


public class RoleIsNotExisteException extends RuntimeException {
    public RoleIsNotExisteException(String message) {
        super(message);
    }
}
