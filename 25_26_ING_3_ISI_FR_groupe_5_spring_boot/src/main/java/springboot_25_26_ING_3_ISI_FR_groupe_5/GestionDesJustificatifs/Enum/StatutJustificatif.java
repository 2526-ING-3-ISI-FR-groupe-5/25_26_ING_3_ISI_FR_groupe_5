package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesJustificatifs.Enum;

public enum StatutJustificatif {
    EN_ATTENTE("En attente"),
    VALIDE("Validé"),
    REFUSE("Refusé");

    private final String libelle;

    StatutJustificatif(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}