package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.DTO.classe;

import lombok.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.inscription.InscriptionResponse;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.programmation.ProgrammationResponse;

import java.util.ArrayList;
import java.util.List;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Classe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Inscription;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.DTO.classes.ClassesResponse;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClassesDetailResponse {

    // ✅ Toutes les infos de base via ClassesResponse
    private ClassesResponse infos;

    // ✅ Listes — uniquement pour le détail
    @Builder.Default
    private List<InscriptionResponse> inscriptions = new ArrayList<>();

    @Builder.Default
    private List<ProgrammationResponse> programmations = new ArrayList<>();
}