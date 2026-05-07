package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.PlageHoraire;


import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Enum.TypeSeance;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Classe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.PlageHoraire;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlageHoraireRequest {

    @NotNull(message = "La classe est obligatoire")
    private Long classeId;

    private Long programmationUEId;

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
    private String titre;

    // ✅ TypeSeance Enum
    @Builder.Default
    private TypeSeance typeSeance = TypeSeance.CM;

    // ✅ Plusieurs enseignants possibles
    @Builder.Default
    private List<Long> enseignantIds = new ArrayList<>();

    // ✅ Jour de fin pour multi-jours
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate jourFin;
}