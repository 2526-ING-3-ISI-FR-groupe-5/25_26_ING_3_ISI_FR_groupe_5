package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.PlageHoraire;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Enum.TypeSeance;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Classe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Filiere;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Specialite;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.UE;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.PlageHoraire;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlageHoraireResponse {

    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate jour;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate jourFin;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime heureDebut;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime heureFin;

    private String salle;
    private String couleur;
    private String titre;

    // ✅ Enum
    private TypeSeance typeSeance;

    // ✅ Champs calculés
    private String titreAffiche;
    private String sousTitreAffiche;
    private long dureeMinutes;
    private long dureeHeures;
    private boolean multiJours;

    // ✅ Statut appel
    private boolean appelEnCours;
    private boolean coursTermine;

    // Relations
    private UEInfo ue;
    private ClasseInfo classe;
    private List<EnseignantInfo> enseignants;
    private Long programmationUEId;

    // ══ Nested DTOs ══

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UEInfo {
        private Long id;
        private String nom;
        private String code;
        private Long nbCredit;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClasseInfo {
        private Long id;
        private String nom;
        private String filiere;
        private int nombreEtudiants;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EnseignantInfo {
        private Long id;
        private String nom;
        private String prenom;
        private String grade;
        private String specialite;
    }
}