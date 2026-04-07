package CarnetRouge.CarnetRouge.GDU.dtos.annee;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnneeResponse {

    private Long id;
    private String nom;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private boolean active;
}