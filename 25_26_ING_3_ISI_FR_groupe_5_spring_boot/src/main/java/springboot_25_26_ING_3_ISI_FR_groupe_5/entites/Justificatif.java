package springboot_25_26_ING_3_ISI_FR_groupe_5.entites;

import jakarta.persistence.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.enums.StatutJustificatif;
import springboot_25_26_ING_3_ISI_FR_groupe_5.enums.TypeJustificatif;
@Entity
public class Justificatif {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String contenu;
    private  StatutJustificatif status;
    private TypeJustificatif  justificatif ;

    public Justificatif(int id, String contenu,  TypeJustificatif justificatif, StatutJustificatif status) {
        this.id = id;

        this.contenu = contenu;
        this.justificatif = justificatif;
        this.status = status;
    }
    @ManyToOne
    @JoinColumn(name = "cours_id", nullable = false)
    private Cours cours;

    public Justificatif() {}

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TypeJustificatif getJustificatif() {
        return justificatif;
    }

    public void setJustificatif(TypeJustificatif justificatif) {
        this.justificatif = justificatif;
    }

    public StatutJustificatif getStatus() {
        return status;
    }

    public void setStatus(StatutJustificatif status) {
        this.status = status;
    }
}
