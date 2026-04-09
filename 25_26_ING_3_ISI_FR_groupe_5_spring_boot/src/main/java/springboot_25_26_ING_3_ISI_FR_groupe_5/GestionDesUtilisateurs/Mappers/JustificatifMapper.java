package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Justificatif;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.justificatif.JustificatifRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.justificatif.JustificatifResponse;

import java.util.List;

@Mapper(componentModel = "spring")
public interface JustificatifMapper {

    @Mapping(target = "etudiantId", source = "etudiant.id")
    @Mapping(target = "etudiantNom", source = "etudiant.nom")
    @Mapping(target = "etudiantPrenom", source = "etudiant.prenom")
    @Mapping(target = "etudiantEmail", source = "etudiant.email")
    @Mapping(target = "etudiantMatricule", source = "etudiant.matricule")
    @Mapping(target = "assistantPedagogiqueId", source = "assistantPedagogique.id")
    @Mapping(target = "assistantPedagogiqueNom", source = "assistantPedagogique.nom")
    @Mapping(target = "assistantPedagogiquePrenom", source = "assistantPedagogique.prenom")
    @Mapping(target = "validateurId", source = "validateur.id")
    @Mapping(target = "validateurNom", source = "validateur.nom")
    @Mapping(target = "validateurPrenom", source = "validateur.prenom")
    @Mapping(target = "validateurEmail", source = "validateur.email")
    JustificatifResponse toResponse(Justificatif justificatif);

    List<JustificatifResponse> toResponseList(List<Justificatif> justificatifs);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateSoumission", ignore = true)
    @Mapping(target = "dateValidation", ignore = true)
    @Mapping(target = "etudiant", ignore = true)
    @Mapping(target = "assistantPedagogique", ignore = true)
    @Mapping(target = "validateur", ignore = true)
    @Mapping(target = "seance", ignore = true)
    @Mapping(target = "fichiers", ignore = true)
    Justificatif toEntity(JustificatifRequest request);
}