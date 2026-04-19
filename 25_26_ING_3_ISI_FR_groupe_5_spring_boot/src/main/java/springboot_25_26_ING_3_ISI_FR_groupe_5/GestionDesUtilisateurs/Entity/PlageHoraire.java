package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Annee_academique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Classe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Semestre;

import java.time.LocalDate;
import java.time.LocalTime;


@Entity
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class PlageHoraire extends Auditable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate jour;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private String salle;
    private String couleur;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semestre_id", nullable = false)
    private Semestre semestre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classe_id")
    private Classe classe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programmation_ue_id")
    private ProgrammationUE programmationUE;

// ══════════════════════════════════════════
    // HELPERS DE VÉRIFICATION
    // ══════════════════════════════════════════

    public boolean isDansAnneeActive(Annee_academique anneeActive) {
        return semestre != null && semestre.getAnneeAcademique().getId().equals(anneeActive.getId());
    }

    public boolean isDansSemestreActif() {
        return semestre != null && semestre.isActif();
    }

    public boolean isConflitAvec(PlageHoraire autre) {
        if (!this.jour.equals(autre.jour)) return false;
        if (this.salle != null && autre.salle != null && !this.salle.equals(autre.salle)) return false;
        if (!this.classe.getId().equals(autre.classe.getId())) return false;

        return (this.heureDebut.isBefore(autre.heureFin) && this.heureFin.isAfter(autre.heureDebut));
    }

    public String getPlageToString() {
        return jour + " " + heureDebut + " - " + heureFin;
    }

    public String getInfoComplete() {
        String ueNom = programmationUE != null && programmationUE.getUe() != null
                ? programmationUE.getUe().getNom() : "N/A";
        String classeNom = classe != null ? classe.getNom() : "N/A";
        return ueNom + " - " + classeNom + " - " + getPlageToString() + " - Salle: " + (salle != null ? salle : "N/A");
    }
}