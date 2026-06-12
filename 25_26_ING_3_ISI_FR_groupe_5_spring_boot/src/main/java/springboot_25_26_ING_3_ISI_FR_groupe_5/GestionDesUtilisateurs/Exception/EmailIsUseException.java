package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exception;


public class EmailIsUseException extends RuntimeException {
    public EmailIsUseException(String message) {
        super(message);
    }
}
