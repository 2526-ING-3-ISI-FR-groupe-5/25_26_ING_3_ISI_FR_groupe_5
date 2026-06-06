package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Mappers;

import org.mapstruct.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.PlageHoraire.PlageHoraireResponse;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.PlageHoraire;

import java.util.List;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Classe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Filiere;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Niveau;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Specialite;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.UE;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.ProgrammationUE;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Enum.TypeSeance;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface PlageHoraireMapper {

    // ============================================
    // PlageHoraire → PlageHoraireResponse
    // ============================================

    // ── Créneau ──
    @Mapping(target = "jourFin", source = "jourFinEffectif")

    // ── Type ──
    @Mapping(target = "typeSeance", source = "typeSeance")
    @Mapping(target = "titre", source = "titre")

    // ── Champs calculés ──
    @Mapping(target = "titreAffiche",    expression = "java(plage.getTitreAffiche())")
    @Mapping(target = "sousTitreAffiche",expression = "java(plage.getSousTitreAffiche())")
    @Mapping(target = "dureeMinutes",    expression = "java(plage.getDureeMinutes())")
    @Mapping(target = "dureeHeures",     expression = "java(plage.getDureeHeures())")
    @Mapping(target = "multiJours",      expression = "java(plage.isMultiJours())")

    // ── Etat d'appel (pour badges UI dans liste-cours.html) ──
    @Mapping(target = "appelEnCours",    expression = "java(plage.isAppelEnCours())")
    @Mapping(target = "coursTermine",    expression = "java(plage.isCoursTermine())")

    // ── UE (via programmationUE) ──
    @Mapping(target = "ue.id",       source = "programmationUE.ue.id")
    @Mapping(target = "ue.nom",      source = "programmationUE.ue.nom")
    @Mapping(target = "ue.code",     source = "programmationUE.ue.code")

    // ── Classe ──
    @Mapping(target = "classe.id",  source = "classe.id")
    @Mapping(target = "classe.nom", source = "classe.nom")

    // ── ProgrammationUE id ──
    @Mapping(target = "programmationUEId", source = "programmationUE.id")

    // ── Enseignants — mappés manuellement via @AfterMapping ──
    @Mapping(target = "enseignants", ignore = true)

    // ── Classe.filiere et nombreEtudiants — mappés via @AfterMapping ──
    @Mapping(target = "classe.filiere", ignore = true)
    @Mapping(target = "classe.nombreEtudiants", ignore = true)

    PlageHoraireResponse toResponse(PlageHoraire plage);

    // ============================================
    // @AfterMapping — champs complexes
    // ============================================

    @AfterMapping
    default void mapChampComplexes(
            @MappingTarget PlageHoraireResponse response,
            PlageHoraire plage) {

        // ── Enseignants ──
        if (plage.getEnseignants() != null
                && !plage.getEnseignants().isEmpty()) {

            var enseignants = plage.getEnseignants().stream()
                    .map(e -> {
                        var info = new PlageHoraireResponse.EnseignantInfo();
                        info.setId(e.getId());
                        info.setNom(e.getNom());
                        info.setPrenom(e.getPrenom());
                        info.setGrade(e.getGrade());
                        // ✅ Specialite ajoutée
                        info.setSpecialite(e.getSpecialite());
                        return info;
                    })
                    .toList();

            response.setEnseignants(enseignants);
        }

        // ── Classe — filiere et nombreEtudiants ──
        if (plage.getClasse() != null) {
            if (response.getClasse() == null) {
                response.setClasse(new PlageHoraireResponse.ClasseInfo());
            }

            // ✅ Filiere via niveau → specialite → filiere
            if (plage.getClasse().getNiveau() != null
                    && plage.getClasse().getNiveau().getSpecialite() != null
                    && plage.getClasse().getNiveau()
                    .getSpecialite().getFiliere() != null) {

                response.getClasse().setFiliere(
                        plage.getClasse().getNiveau()
                                .getSpecialite().getFiliere().getNom()
                );
            }

            // ✅ Nombre d'étudiants actifs
            response.getClasse().setNombreEtudiants(
                    plage.getClasse().getNombreEtudiants()
            );
        }
    }

    // ============================================
    // Liste
    // ============================================

    List<PlageHoraireResponse> toResponseList(List<PlageHoraire> plages);
}