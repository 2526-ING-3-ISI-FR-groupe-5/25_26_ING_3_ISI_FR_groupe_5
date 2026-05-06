package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers;

import org.mapstruct.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Justificatif;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.justificatif.JustificatifResponse;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface JustificatifMapper {

    // ── Étudiant ──
    @Mapping(target = "etudiantId",       source = "etudiant.id")
    @Mapping(target = "etudiantNom",      source = "etudiant.nom")
    @Mapping(target = "etudiantPrenom",   source = "etudiant.prenom")
    @Mapping(target = "etudiantEmail",    source = "etudiant.email")
    @Mapping(target = "etudiantMatricule",source = "etudiant.matricule")

    // ── Assistant pédagogique ──
    @Mapping(target = "assistantPedagogiqueId",     source = "assistantPedagogique.id")
    @Mapping(target = "assistantPedagogiqueNom",    source = "assistantPedagogique.nom")
    @Mapping(target = "assistantPedagogiquePrenom", source = "assistantPedagogique.prenom")

    // ── Validateur ──
    @Mapping(target = "validateurId",     source = "validateur.id")
    @Mapping(target = "validateurNom",    source = "validateur.nom")
    @Mapping(target = "validateurPrenom", source = "validateur.prenom")
    @Mapping(target = "validateurEmail",  source = "validateur.email")

    // ── statut → status (noms différents) ──
    @Mapping(target = "status", source = "statut")

    JustificatifResponse toResponse(Justificatif j);

    List<JustificatifResponse> toResponseList(List<Justificatif> list);
}