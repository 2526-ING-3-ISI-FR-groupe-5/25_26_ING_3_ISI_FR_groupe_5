package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.inscription.InscriptionRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.inscription.InscriptionResponse;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Inscription;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InscriptionMapper {

    @Mapping(target = "etudiantId", source = "etudiant.id")
    @Mapping(target = "etudiantNom", source = "etudiant.nom")
    @Mapping(target = "etudiantPrenom", source = "etudiant.prenom")
    @Mapping(target = "etudiantMatricule", source = "etudiant.matricule")
    @Mapping(target = "etudiantEmail", source = "etudiant.email")
    @Mapping(target = "classeId", source = "classe.id")
    @Mapping(target = "classeNom", source = "classe.nom")
    @Mapping(target = "anneeAcademiqueId", source = "anneeAcademique.id")
    @Mapping(target = "anneeAcademiqueNom", source = "anneeAcademique.nom")
    InscriptionResponse toResponse(Inscription inscription);

    List<InscriptionResponse> toResponseList(List<Inscription> inscriptions);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "etudiant", ignore = true)
    @Mapping(target = "classe", ignore = true)
    @Mapping(target = "anneeAcademique", ignore = true)
    @Mapping(target = "statut", ignore = true)
    @Mapping(target = "decisionFinAnnee", ignore = true)

    Inscription toEntity(InscriptionRequest request);
}