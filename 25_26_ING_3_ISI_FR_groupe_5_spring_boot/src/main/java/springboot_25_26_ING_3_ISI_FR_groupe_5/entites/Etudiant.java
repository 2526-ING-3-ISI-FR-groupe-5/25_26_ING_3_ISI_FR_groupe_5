package springboot_25_26_ING_3_ISI_FR_groupe_5.entites;

import jakarta.persistence.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.enums.TypeNiveau;
import springboot_25_26_ING_3_ISI_FR_groupe_5.enums.TypeSexe;
import java.util.Date;

@Entity
public class Etudiant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nom;
    private String prenom;
    private String email;
    private String adresse;
    private String telephone;
    private String matricule;

    @Enumerated(EnumType.STRING)
    private TypeNiveau niveau;

    @Enumerated(EnumType.STRING)
    private TypeSexe sexe;

    @Temporal(TemporalType.DATE)
    private Date date_naissance;

    @ManyToOne
    @JoinColumn(name = "filiere_id")
    private Filiere filiere;

    public Etudiant() {}

    public Etudiant(int id, String nom, String prenom, String email, String adresse,
                    TypeSexe sexe, String matricule, TypeNiveau niveau,
                    String telephone, Date date_naissance) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.adresse = adresse;
        this.telephone = telephone;
        this.matricule = matricule;
        this.niveau = niveau;
        this.sexe = sexe;
        this.date_naissance = date_naissance;
    }

    // --- Getters & Setters ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getMatricule() { return matricule; }
    public void setMatricule(String matricule) { this.matricule = matricule; }

    public TypeNiveau getNiveau() { return niveau; }
    public void setNiveau(TypeNiveau niveau) { this.niveau = niveau; }

    public TypeSexe getSexe() { return sexe; }
    public void setSexe(TypeSexe sexe) { this.sexe = sexe; }

    public Date getDate_naissance() { return date_naissance; }
    public void setDate_naissance(Date date_naissance) { this.date_naissance = date_naissance; }

    public Filiere getFiliere() { return filiere; }
    public void setFiliere(Filiere filiere) { this.filiere = filiere; }
}
