package springboot_25_26_ING_3_ISI_FR_groupe_5.entites;

import jakarta.persistence.*;

@Entity

public class SeanceCours {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  int id ;
    private  String titre;
    private int  nb_heure;
    private  int nb_credit;



    // Constructeur
    public SeanceCours() {}

    public SeanceCours(int id, int nb_credit, int nb_heure, String titre) {
        this.id = id;
        this.nb_credit = nb_credit;
        this.nb_heure = nb_heure;
        this.titre = titre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNb_credit() {
        return nb_credit;
    }

    public void setNb_credit(int nb_credit) {
        this.nb_credit = nb_credit;
    }

    public int getNb_heure() {
        return nb_heure;
    }

    public void setNb_heure(int nb_heure) {
        this.nb_heure = nb_heure;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }
}
