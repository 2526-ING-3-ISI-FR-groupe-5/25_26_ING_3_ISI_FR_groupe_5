package springboot_25_26_ING_3_ISI_FR_groupe_5.entites;

import jakarta.persistence.*;

import java.util.List;
@Entity
public class Administrateur extends Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private  int id;
    private String nom;
    private String email;
    private  String telephone;





    public Administrateur() {}
    public Administrateur(String email, int id, String nom, String telephone) {
        this.email = email;
        this.id = id;
        this.nom = nom;
        this.telephone = telephone;
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
