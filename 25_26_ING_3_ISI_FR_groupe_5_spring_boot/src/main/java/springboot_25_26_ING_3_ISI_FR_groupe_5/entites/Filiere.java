package springboot_25_26_ING_3_ISI_FR_groupe_5.entites;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Filiere {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nom;
    private String code;
    private String niveau;
    private String description;



    public Filiere() {}

    public Filiere(int id, String code, String description, String niveau, String nom) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.niveau = niveau;
        this.nom = nom;
    }

    // --- Getters & Setters ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getNiveau() { return niveau; }
    public void setNiveau(String niveau) { this.niveau = niveau; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }


}
