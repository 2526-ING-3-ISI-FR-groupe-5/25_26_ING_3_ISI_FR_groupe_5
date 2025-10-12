package springboot_25_26_ING_3_ISI_FR_groupe_5.entites;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Ecole {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
    private  int id;
    private  String nom;
    private  String adresse;
    private  String email;
    private  String telephone;
    @ManyToOne
    @JoinColumn(name = "institut_id", nullable = false)
    private Institut institut;

    @ManyToOne
    @JoinColumn(name = "administrateur_id", nullable = false)
    private Administrateur administrateur;

    @OneToMany(mappedBy = "ecole", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AssistantPedagogique> assistantPedagogiques;
    public Ecole() {}
    public Ecole(String adresse, String email, int id, String nom, String telephone) {
        this.adresse = adresse;
        this.email = email;
        this.id = id;
        this.nom = nom;
        this.telephone = telephone;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}
