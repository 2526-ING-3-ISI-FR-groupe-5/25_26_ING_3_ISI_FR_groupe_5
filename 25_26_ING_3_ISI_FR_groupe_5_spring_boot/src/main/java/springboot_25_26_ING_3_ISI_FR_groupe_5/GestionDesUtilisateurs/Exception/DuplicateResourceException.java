package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exception;

import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Cycle;

public class DuplicateResourceException extends RuntimeException{
    public DuplicateResourceException(String cycle, Object libelle) {
        super(cycle+": "+libelle);
    }
}
