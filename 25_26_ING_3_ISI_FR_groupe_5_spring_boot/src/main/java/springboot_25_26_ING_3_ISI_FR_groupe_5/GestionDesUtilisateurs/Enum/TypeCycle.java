package CarnetRouge.CarnetRouge.GDU.Enum;


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
