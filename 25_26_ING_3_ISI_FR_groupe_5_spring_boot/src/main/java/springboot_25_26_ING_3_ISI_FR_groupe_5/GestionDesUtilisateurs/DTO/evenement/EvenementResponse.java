package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.evenement;

import CarnetRouge.CarnetRouge.GDU.Enum.TypeEvenement;


import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EvenementResponse {

    private Long id;
    private String nom;
    private String description;
    private String couleur;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private TypeEvenement type;
    private Long anneeAcademiqueId;
    private String anneeAcademiqueNom;
}
