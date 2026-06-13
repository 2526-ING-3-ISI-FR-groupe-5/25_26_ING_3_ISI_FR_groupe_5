package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesJustificatifs.Mappers;

import org.mapstruct.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesJustificatifs.Entity.Justificatif;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesJustificatifs.DTO.justificatif.JustificatifResponse;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.Appels;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    // ── statut -> status ──
    @Mapping(target = "status", source = "statut")

    // ── Mapping personnalisé pour lister les matières justifiées ──
    @Mapping(target = "seancesJustifiees", source = "appels", qualifiedByName = "mapAppelsToSeances")
    JustificatifResponse toResponse(Justificatif j);

    List<JustificatifResponse> toResponseList(List<Justificatif> list);

    // Méthode utilitaire de mapping pour extraire les noms des cours impactés
    @Named("mapAppelsToSeances")
    default List<String> mapAppelsToSeances(Set<Appels> appels) {
        if (appels == null) return List.of();
        return appels.stream()
                .filter(a -> a.getPlageHoraire() != null)
                .map(a -> a.getPlageHoraire().getTitreAffiche() + " (" + a.getPlageHoraire().getJour().toString() + ")")
                .distinct()
                .collect(Collectors.toList());
    }
}