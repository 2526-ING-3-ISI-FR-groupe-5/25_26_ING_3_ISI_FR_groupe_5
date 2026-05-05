package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers;

import org.mapstruct.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.sessionAppel.SessionAppelResponse;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.SessionAppel;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface SessionAppelMapper {

    @Mapping(target = "plageHoraireId",   source = "plageHoraire.id")
    @Mapping(target = "plageHoraireTitre",source = "plageHoraire.titreAffiche")
    @Mapping(target = "plageHoraireJour", source = "plageHoraire.jour")

    @Mapping(target = "enseignantId",     source = "enseignant.id")
    @Mapping(target = "enseignantNom",    source = "enseignant.nom")
    @Mapping(target = "enseignantPrenom", source = "enseignant.prenom")

    // Calculés via @AfterMapping
    @Mapping(target = "expire",             ignore = true)
    @Mapping(target = "nbPresents",         ignore = true)
    @Mapping(target = "nbAbsents",          ignore = true)
    @Mapping(target = "nbPartiels",         ignore = true)
    @Mapping(target = "nbRetards",          ignore = true)
    @Mapping(target = "retardMoyenMinutes", ignore = true)
    @Mapping(target = "retardAutorise",     ignore = true)
    @Mapping(target = "appelComplet",       ignore = true)
    @Mapping(target = "tauxPresence",       ignore = true)
    @Mapping(target = "totalEtudiants",     ignore = true)
    @Mapping(target = "plageHoraireHeures", ignore = true)
    @Mapping(target = "nbHeureTotal",       ignore = true)

    SessionAppelResponse toResponse(SessionAppel session);

    List<SessionAppelResponse> toResponseList(List<SessionAppel> sessions);

    @AfterMapping
    default void completer(@MappingTarget SessionAppelResponse r, SessionAppel s) {

        // Validité
        r.setExpire(s.isExpire());

        // Retard autorisé sur ce cours
        r.setRetardAutorise(s.estPremierCoursDuMatin());

        // Stats appels
        r.setNbPresents((int) s.getNbPresents());
        r.setNbAbsents((int) s.getNbAbsents());
        r.setNbPartiels((int) s.getNbPartiels());
        r.setNbRetards((int) s.getNbRetards());
        r.setTotalEtudiants(s.getAppels().size());
        r.setRetardMoyenMinutes(s.getRetardMoyenMinutes());
        r.setAppelComplet(s.isAppelComplet());
        r.setTauxPresence(s.getTauxPresence());

        // Heures plage
        if (s.getPlageHoraire() != null) {
            var p = s.getPlageHoraire();
            r.setPlageHoraireHeures(p.getHeureDebut() + " – " + p.getHeureFin());
            r.setNbHeureTotal(p.getDureeMinutes() / 60);
        }
    }
}