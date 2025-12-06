package springboot_25_26_ING_3_ISI_FR_groupe_5.entites;

import jakarta.persistence.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.enums.TypeNiveau;

@Entity
public class Cycle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  int id;
    @Enumerated(EnumType.STRING)
    private TypeNiveau niveau;
    private String  specialite;

    public Cycle(int id, TypeNiveau niveau, String specialite) {
        this.id = id;
        this.niveau = niveau;
        this.specialite = specialite;
    }

    private Administrateur administrateur;
    private Filiere filiere;

    // Constructeur
    public Cycle() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TypeNiveau getNiveau() {
        return niveau;
    }

    public void setNiveau(TypeNiveau niveau) {
        this.niveau = niveau;
    }

    public String getSpecialite() {
        return specialite;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }
}
