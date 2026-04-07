package springboot_25_26_ING_3_ISI_FR_groupe_5.Enums;

import lombok.Getter;

@Getter
public enum TypeCycle {
    LICENCE("Licence"),
    MASTER("Master"),
    DOCTORAT("Doctorat"),
    INGENIEUR("Ingénieur");

    private final String libelle;

    TypeCycle(String libelle) {
        this.libelle = libelle;
    }

}