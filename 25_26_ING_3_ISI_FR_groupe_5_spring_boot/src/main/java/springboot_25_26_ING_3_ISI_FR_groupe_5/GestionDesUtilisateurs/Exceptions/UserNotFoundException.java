package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String  message) {
        super(message);
    }
}
