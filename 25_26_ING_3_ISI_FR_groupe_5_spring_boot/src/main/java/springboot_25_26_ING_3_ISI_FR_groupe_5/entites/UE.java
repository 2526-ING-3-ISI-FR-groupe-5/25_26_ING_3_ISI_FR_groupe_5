package springboot_25_26_ING_3_ISI_FR_groupe_5.entites;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
public class UE {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private  String nom;
    private String code;
    private int nb_heure;
    private int  credit;

    public UE(int id,  String nom , String code,  int nb_heure, int credit) {
        this.id = id;
        this.nom = nom;
        this.code = code;

        this.nb_heure = nb_heure;
        this.credit = credit;


    }
    @OneToMany(mappedBy = "ue", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cours> cours;

    // Relation : Appartenir à 1 Filiere
    @ManyToOne
    @JoinColumn(name = "filiere_id", nullable = false)
    private Filiere filiere;

    // Relation : Gerer par 1..* Enseignant
    @ManyToMany(mappedBy = "ues")
    private List<Enseignant> enseignants;

    // Constructeur
    public UE() {}
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNb_heure() {
        return nb_heure;
    }

    public void setNb_heure(int nb_heure) {
        this.nb_heure = nb_heure;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
}
