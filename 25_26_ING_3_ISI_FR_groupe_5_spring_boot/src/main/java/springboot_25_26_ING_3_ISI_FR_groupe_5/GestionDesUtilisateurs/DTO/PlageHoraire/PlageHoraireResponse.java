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
    private String classeNom;
    private String ueNom;
    private String ueCode;
    private List<String> enseignantsNoms;
}
