package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Annee_Academique;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class AnneeRequest {

    private String nom;  // Auto-généré à partir des dates

    @NotNull(message = "La date de début est obligatoire")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateDebut;

    @NotNull(message = "La date de fin est obligatoire")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateFin;

    private boolean active;

    // ✅ Contrainte 1 : dateDebut doit être avant dateFin
    @AssertTrue(message = "La date de début doit être antérieure à la date de fin")
    public boolean isDateDebutBeforeDateFin() {
        if (dateDebut == null || dateFin == null) {
            return true;
        }
        return dateDebut.isBefore(dateFin);
    }

    // ✅ Contrainte 2 : dateDebut ne peut pas être dans le passé
    @AssertTrue(message = "La date de début ne peut pas être dans le passé")
    public boolean isDateDebutNotPast() {
        if (dateDebut == null) {
            return true;
        }
        return !dateDebut.isBefore(LocalDate.now());
    }

    // ✅ Générer automatiquement le nom à partir des dates
    public String getNom() {
        if (dateDebut != null && dateFin != null) {
            int anneeDebut = dateDebut.getYear();
            int anneeFin = dateFin.getYear();
            return anneeDebut + "-" + anneeFin;
        }
        return nom;
    }

}