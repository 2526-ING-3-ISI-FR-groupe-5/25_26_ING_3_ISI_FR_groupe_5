package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum;

import lombok.Getter;

@Getter
public enum TypeNiveau {
    I, II, III, IV, V;

    public enum StatutInscription {
        ACTIF,
        SUSPENDU,
        ABANDONNE
    }
}