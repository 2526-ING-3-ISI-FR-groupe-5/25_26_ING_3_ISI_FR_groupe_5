package springboot_25_26_ING_3_ISI_FR_groupe_5.entites;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity

public class FICHE_PRESENCE {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private Date date;
    private Date date_debut;
    private Date date_fin;
    @OneToMany(mappedBy = "fichePresence", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cours> cours;

    public FICHE_PRESENCE() {}
    public FICHE_PRESENCE(int id, Date date, Date date_debut, Date date_fin ) {
        this.id = id;

        this.date = date;
        this.date_debut = date_debut;
        this.date_fin = date_fin;
    }



    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate_debut() {
        return date_debut;
    }

    public void setDate_debut(Date date_debut) {
        this.date_debut = date_debut;
    }

    public Date getDate_fin() {
        return date_fin;
    }

    public void setDate_fin(Date date_fin) {
        this.date_fin = date_fin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
