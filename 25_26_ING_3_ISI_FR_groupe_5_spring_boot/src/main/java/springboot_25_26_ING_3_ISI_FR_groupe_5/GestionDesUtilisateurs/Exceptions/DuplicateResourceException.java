package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exceptions;

public class DuplicateResourceException extends RuntimeException{
    public DuplicateResourceException(String cycle, Object libelle) {
        super(cycle+": "+libelle);
    }
}
