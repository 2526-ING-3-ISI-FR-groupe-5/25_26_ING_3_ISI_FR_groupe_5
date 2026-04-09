package springboot_25_26_ING_3_ISI_FR_groupe_5.Enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum TypeCycle {
    LICENCE("Licence"),
    MASTER("Master"),
    DOCTORAT("Doctorat"),
    INGENIEUR("Ingénieur");

    @JsonValue
    private final String libelle;

    TypeCycle(String libelle) {
        this.libelle = libelle;
    }

    @JsonCreator
    public static TypeCycle fromValue(String value) {
        if (value == null) return null;
        for (TypeCycle t : values()) {
            if (t.name().equalsIgnoreCase(value) || t.libelle.equalsIgnoreCase(value)) {
                return t;
            }
        }
        throw new IllegalArgumentException("TypeCycle inconnu : " + value);
    }
}