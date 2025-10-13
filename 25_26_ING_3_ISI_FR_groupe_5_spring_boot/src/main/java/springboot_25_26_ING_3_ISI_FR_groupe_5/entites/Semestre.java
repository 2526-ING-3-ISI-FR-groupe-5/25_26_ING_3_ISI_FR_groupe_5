package springboot_25_26_ING_3_ISI_FR_groupe_5.entites;

import jakarta.persistence.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.enums.Num_semestre;

import java.util.Date;
import java.util.List;

@Entity
public class Semestre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  int id;
    @Enumerated()

    private Num_semestre semestre;
    private Date date_debut;
    private Date date_fin;
    @OneToMany(mappedBy = "semestre", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UE> ues;

    @OneToMany(mappedBy = "semestre", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cours> cours;

    @ManyToOne
    @JoinColumn(name = "annee_academique_id", nullable = false)
    private Annee_academique anneeAcademique;

    public Semestre() {}
    public Semestre(int id, Date date_debut, Date date_fin,  Num_semestre semestre) {
        this.id = id;
        this.semestre = semestre;

        this.date_debut = date_debut;
        this.date_fin = date_fin;
    }

    public Date getDate_debut() {
        return date_debut;
    }

    public void setDate_debut(Date date_debut) {
        this.date_debut = date_debut;
    }

    public Date getDate_fin() {
        return date_fin;
    }

    public void setDate_fin(Date date_fin) {
        this.date_fin = date_fin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Num_semestre getSemestre() {
        return semestre;
    }

    public void setSemestre(Num_semestre semestre) {
        this.semestre = semestre;
    }
}
//1FQCOMJH9J8@2025