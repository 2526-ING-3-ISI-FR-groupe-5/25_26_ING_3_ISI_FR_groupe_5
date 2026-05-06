package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers;

import org.mapstruct.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Appels;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.appel.AppelsResponse;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface AppelsMapper {

    @Mapping(target = "etudiantId",       source = "etudiant.id")
    @Mapping(target = "etudiantNom",      source = "etudiant.nom")
    @Mapping(target = "etudiantPrenom",   source = "etudiant.prenom")
    @Mapping(target = "etudiantMatricule",source = "etudiant.matricule")

    @Mapping(target = "plageHoraireId",   source = "plageHoraire.id")
    @Mapping(target = "plageHoraireTitre",source = "plageHoraire.titreAffiche")
    @Mapping(target = "plageHoraireJour", source = "plageHoraire.jour")

    @Mapping(target = "enseignantId",     source = "enseignant.id")
    @Mapping(target = "enseignantNom",    source = "enseignant.nom")
    @Mapping(target = "enseignantPrenom", source = "enseignant.prenom")

    @Mapping(target = "sessionAppelId",   source = "sessionAppel.id")
    @Mapping(target = "sessionMethode",   source = "sessionAppel.methode")

    @Mapping(target = "justificatifId",   source = "justificatif.id")

    // 🆕 Retard
    @Mapping(target = "heureArrivee",     source = "heureArrivee")
    @Mapping(target = "retardMinutes",    source = "retardMinutes")       // helper Appels
    @Mapping(target = "retardAutorise",   source = "retardAutorise")      // helper Appels

    // Calculés — via @AfterMapping
    @Mapping(target = "statutLibelle",       ignore = true)
    @Mapping(target = "plageHoraireHeures",  ignore = true)
    @Mapping(target = "institutId",          ignore = true)
    @Mapping(target = "institutNom",         ignore = true)
    @Mapping(target = "justificatifStatut",  ignore = true)
    @Mapping(target = "nbHeuresAbsent",      ignore = true)
    @Mapping(target = "totalHeures",         ignore = true)

    AppelsResponse toResponse(Appels appel);

    List<AppelsResponse> toResponseList(List<Appels> appels);

    @AfterMapping
    default void completer(@MappingTarget AppelsResponse r, Appels a) {

        // Libellé statut
        r.setStatutLibelle(a.getStatutLibelle());

        // Heures plage
        if (a.getPlageHoraire() != null) {
            var p = a.getPlageHoraire();
            r.setPlageHoraireHeures(p.getHeureDebut() + " – " + p.getHeureFin());

            // Calcul nbHeuresAbsent / totalHeures
            long duree = p.getDureeMinutes() / 60;
            r.setTotalHeures((int) duree);
            r.setNbHeuresAbsent((int) Math.max(0, duree - a.getNbHeuresPresent()));
        }

        // Institut via étudiant
        if (a.getEtudiant() != null && a.getEtudiant().getInstitut() != null) {
            var inst = a.getEtudiant().getInstitut();
            r.setInstitutId(inst.getId());
            r.setInstitutNom(inst.getNom());
        }

        // Justificatif statut
        if (a.getJustificatif() != null) {
            r.setJustificatifStatut(a.getJustificatif().getStatut() != null
                    ? a.getJustificatif().getStatut().name() : null);
        }
    }
}