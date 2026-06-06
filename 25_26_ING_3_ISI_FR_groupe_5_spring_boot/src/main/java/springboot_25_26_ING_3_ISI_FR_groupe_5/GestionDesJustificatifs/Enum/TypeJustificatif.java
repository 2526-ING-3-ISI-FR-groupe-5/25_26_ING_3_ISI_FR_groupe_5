package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesJustificatifs.Enum;

public enum TypeJustificatif {
    MALADIE("Maladie"),
    ACCIDENT("Accident"),
    DEUIL("Deuil familial"),
    AUTRE("Autre");

    private final String libelle;

    TypeJustificatif(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
