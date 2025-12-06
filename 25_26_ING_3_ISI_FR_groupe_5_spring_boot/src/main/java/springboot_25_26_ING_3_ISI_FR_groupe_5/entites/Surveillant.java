package springboot_25_26_ING_3_ISI_FR_groupe_5.entites;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Surveillant  extends  Utilisateur{
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
        private  int id;
        private String nom;
        private String email;
        private  String telephone;

    public Surveillant() {}
    public Surveillant(int id,String email,  String nom, String telephone) {
        this.id = id;

        this.email = email;
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
