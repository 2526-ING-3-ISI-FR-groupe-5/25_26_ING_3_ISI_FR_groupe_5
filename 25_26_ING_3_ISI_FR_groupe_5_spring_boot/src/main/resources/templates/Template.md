package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.PlageHoraire;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class PlageHoraireDragDropRequest {

    @NotNull(message = "L'UE est obligatoire")
    private Long ueId;

    @NotNull(message = "La classe est obligatoire")
    private Long classeId;

    @NotNull(message = "Le jour est obligatoire")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate jour;

    @NotNull(message = "L'heure de début est obligatoire")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime heureDebut;

    @NotNull(message = "L'heure de fin est obligatoire")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime heureFin;

    private String salle;
    private String couleur;
    private Long enseignantId;
}
--------------------
package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.PlageHoraire;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Getter
@Setter
public class PlageHoraireRecurrenceRequest {

    @NotNull(message = "L'UE est obligatoire")
    private Long ueId;

    @NotNull(message = "La classe est obligatoire")
    private Long classeId;

    @NotNull(message = "L'heure de début est obligatoire")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime heureDebut;

    @NotNull(message = "L'heure de fin est obligatoire")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime heureFin;

    private String salle;
    private String couleur;
    private Long enseignantId;

    @NotNull(message = "Les jours de la semaine sont obligatoires")
    private Set<DayOfWeek> jours;  // [LUNDI, MARDI, ...]

    @NotNull(message = "La date de début est obligatoire")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateDebut;

    @NotNull(message = "La date de fin est obligatoire")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateFin;
}
-----------------
package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.PlageHoraire;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class PlageHoraireRequest {

    private Long classeId;
    private Long programmationUEId;
    private Long ueId;
    private Long enseignantId;
    private Long semestreId;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate jour;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime heureDebut;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime heureFin;

    private String salle;
    private String couleur;
    private String typeSeance;
    private String titre;
}
-------------
package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.PlageHoraire;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlageHoraireResponse {

    private Long id;
    private LocalDate jour;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private String salle;
    private String couleur;
    private String titre;
    private String typeSeance;
    private String description;
    private Long nbHeure;
    private Long nbCredit;

    // Classe
    private Long classeId;
    private String classeNom;

    // UE
    private Long ueId;
    private String ueNom;
    private String ueCode;

    // Enseignants
    private List<String> enseignantsNoms;
    private List<Long> enseignantsIds;

    // Semestre
    private Long semestreId;
    private String semestreNom;

    // Institut
    private Long institutId;
    private String institutNom;
}