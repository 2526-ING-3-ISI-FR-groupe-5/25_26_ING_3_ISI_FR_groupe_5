package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Enum;

import lombok.Getter;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Semestre;

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

    public static TypeSemestre fromNumero(int numero) {
        for (TypeSemestre s : values()) {
            if (s.numero == numero) {
                return s;
            }
        }
        throw new IllegalArgumentException("Numéro de semestre invalide : " + numero);
    }
}