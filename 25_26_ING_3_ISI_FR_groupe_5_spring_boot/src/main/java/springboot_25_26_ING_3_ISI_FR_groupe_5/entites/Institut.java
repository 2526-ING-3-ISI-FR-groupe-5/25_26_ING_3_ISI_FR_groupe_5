package springboot_25_26_ING_3_ISI_FR_groupe_5.entites;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Institut {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
    private  int id;
    private  String nom;
    private  String ville;
    private  String adresse;
    private  String email;
    private  String telephone;
    private  String localite;


    public Institut(int id, String adresse, String email,  String localite, String nom, String telephone, String ville) {
        this.id = id;

        this.adresse = adresse;
        this.email = email;
        this.localite = localite;
        this.nom = nom;
        this.telephone = telephone;
        this.ville = ville;
    }
    @OneToMany(mappedBy = "institut", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ecole> ecoles;

    @ManyToOne
    @JoinColumn(name = "administrateur_id", nullable = false)
    private Administrateur administrateur;

    public Institut() {}

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
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

    public String getLocalite() {
        return localite;
    }

    public void setLocalite(String localite) {
        this.localite = localite;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }
}
