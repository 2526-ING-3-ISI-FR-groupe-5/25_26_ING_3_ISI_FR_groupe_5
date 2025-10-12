package springboot_25_26_ING_3_ISI_FR_groupe_5.entites;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity

public class Cours {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  int id ;
    private  String titre;
    private int  nb_heure;
    private  int nb_credit;

    @ManyToOne
    @JoinColumn(name = "fiche_presence_id", nullable = false)
    private FICHE_PRESENCE fichePresence;

    // Relation : Etabli pour 1..* Justificatif
    @OneToMany(mappedBy = "cours", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Justificatif> justificatifs;

    // Relation : Englober par 1 Semestre
    @ManyToOne
    @JoinColumn(name = "semestre_id", nullable = false)
    private Semestre semestre;

    // Relation : Contenu dans 1 UE
    @ManyToOne
    @JoinColumn(name = "ue_id", nullable = false)
    private UE ue;

    // Relation : Dispenser par 1 Enseignant
    @ManyToOne
    @JoinColumn(name = "enseignant_id", nullable = false)
    private Enseignant enseignant;

    // Constructeur
    public Cours() {}

    public Cours(int id, int nb_credit, int nb_heure, String titre) {
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
