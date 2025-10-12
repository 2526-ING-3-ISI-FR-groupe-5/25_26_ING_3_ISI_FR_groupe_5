package springboot_25_26_ING_3_ISI_FR_groupe_5.entites;

import jakarta.persistence.*;

@Entity
public class AssistantPedagogique {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  int id;
    private String nom;
    private String email;

    public AssistantPedagogique(String email, int id, String nom) {
        this.email = email;
        this.id = id;
        this.nom = nom;
    }
    @ManyToOne
    @JoinColumn(name = "ecole_id", nullable = false)
    private Ecole ecole;

    @ManyToOne
    @JoinColumn(name = "administrateur_id", nullable = false)
    private Administrateur administrateur;

    public AssistantPedagogique() {}


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
}
