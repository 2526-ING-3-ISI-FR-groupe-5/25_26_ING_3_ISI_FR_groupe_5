package CarnetRouge.CarnetRouge.GDU.Enum;

import lombok.Getter;

@Getter
public enum TypeSemestre {
    SEMESTRE_1("Semestre 1", 1),
    SEMESTRE_2("Semestre 2", 2);

    private final String libelle;
    private final int numero;

    TypeSemestre(String libelle, int numero) {
        this.libelle = libelle;
        this.numero = numero;
    }

    // ✅ Pour convertir un numéro en enum
    public static TypeSemestre fromNumero(int numero) {
        for (TypeSemestre s : values()) {
            if (s.numero == numero) {
                return s;
            }
        }
        throw new IllegalArgumentException("Numéro de semestre invalide : " + numero);
    }
}