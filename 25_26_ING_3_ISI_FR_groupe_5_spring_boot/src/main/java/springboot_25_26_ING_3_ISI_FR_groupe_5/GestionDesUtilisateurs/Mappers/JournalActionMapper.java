
package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers;

import org.mapstruct.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.journal.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.JournalAction;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.TypeAction;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface JournalActionMapper {

    // ============================================
    // JournalAction → JournalActionResponse
    // ============================================

    @Mapping(target = "utilisateurId",
            source = "utilisateur.id")
    @Mapping(target = "utilisateurNom",
            source = "utilisateur.nom",
            defaultValue = "Inconnu")
    @Mapping(target = "utilisateurPrenom",
            source = "utilisateur.prenom",
            defaultValue = "Inconnu")
    @Mapping(target = "utilisateurEmail",
            source = "utilisateur.email",
            defaultValue = "Inconnu")
    @Mapping(target = "utilisateurType",
            expression = "java(getUtilisateurType(journalAction.getUtilisateur()))")
    @Mapping(target = "typeActionLibelle",
            expression = "java(getLibelleTypeAction(journalAction.getTypeAction()))")
    JournalActionResponse toResponse(JournalAction journalAction);

    // ============================================
    // JournalActionRequest → JournalAction
    // ============================================

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "adresseIp", ignore = true)
    @Mapping(target = "navigateur", ignore = true)
    @Mapping(target = "dateAction", ignore = true)
    @Mapping(target = "utilisateur", ignore = true)
    JournalAction toEntity(JournalActionRequest request);

    // ============================================
    // Object[] → JournalStatsResponse
    // ============================================

    default JournalStatsResponse toStatsResponse(Object[] row) {
        if (row == null || row.length < 2) return null;

        TypeAction typeAction = (TypeAction) row[0];
        return JournalStatsResponse.builder()
                .typeAction(typeAction)
                .typeActionLibelle(getLibelleTypeAction(typeAction))
                .nombre((Long) row[1])
                .build();
    }

    // ============================================
    // Object[] → JournalEchecResponse
    // ============================================

    default JournalEchecResponse toEchecResponse(Object[] row) {
        if (row == null || row.length < 4) return null;

        return JournalEchecResponse.builder()
                .utilisateurId((Long) row[0])
                .utilisateurNom((String) row[1])
                .utilisateurPrenom((String) row[2])
                .nombreEchecs((Long) row[3])
                .build();
    }

    // ============================================
    // Object[] → IpSuspecteResponse
    // ============================================

    default IpSuspecteResponse toIpSuspecteResponse(Object[] row) {
        if (row == null || row.length < 2) return null;

        return IpSuspecteResponse.builder()
                .adresseIp((String) row[0])
                .nombreTentatives((Long) row[1])
                .build();
    }

    // ============================================
    // Méthodes utilitaires — default
    // ============================================

    default String getUtilisateurType(Utilisateur utilisateur) {
        if (utilisateur == null) return "Inconnu";
        return utilisateur.getClass().getSimpleName();
    }

    default String getLibelleTypeAction(TypeAction typeAction) {
        if (typeAction == null) return "Inconnu";

        return switch (typeAction) {

            // Authentification
            case CONNEXION -> "Connexion";
            case DECONNEXION -> "Déconnexion";
            case TENTATIVE_CONNEXION_ECHOUEE -> "Tentative de connexion échouée";
            case CHANGEMENT_MOT_DE_PASSE -> "Changement de mot de passe";
            case COMPTE_ACTIVE -> "Compte activé";
            case COMPTE_DESACTIVE -> "Compte désactivé";
            case COMPTE_VERROUILLE -> "Compte verrouillé";

            // Utilisateurs
            case UTILISATEUR_CREE -> "Utilisateur créé";
            case UTILISATEUR_MODIFIE -> "Utilisateur modifié";
            case UTILISATEUR_DESACTIVE -> "Utilisateur désactivé";

            // Année académique
            case ANNEE_ACADEMIQUE_CREEE -> "Année académique créée";
            case ANNEE_ACADEMIQUE_MODIFIEE -> "Année académique modifiée";
            case ANNEE_ACADEMIQUE_ACTIVEE -> "Année académique activée";
            case ANNEE_ACADEMIQUE_SUPPRIMEE -> "Année académique supprimée";

            // Semestre
            case SEMESTRE_CREE -> "Semestre créé";
            case SEMESTRE_ACTIVE -> "Semestre activé";
            case SEMESTRE_DESACTIVE -> "Semestre désactivé";
            case SEMESTRE_SUPPRIME -> "Semestre supprimé";

            // Ecole / Institut
            case ECOLE_CREEE -> "École créée";
            case ECOLE_MODIFIEE -> "École modifiée";
            case ECOLE_SUPPRIMEE -> "École supprimée";
            case INSTITUT_CREE -> "Institut créé";
            case INSTITUT_MODIFIE -> "Institut modifié";
            case INSTITUT_SUPPRIME -> "Institut supprimé";

            // Filière / Cycle
            case FILIERE_CREEE -> "Filière créée";
            case FILIERE_MODIFIEE -> "Filière modifiée";
            case FILIERE_SUPPRIMEE -> "Filière supprimée";
            case CYCLE_CREE -> "Cycle créé";
            case CYCLE_MODIFIE -> "Cycle modifié";
            case CYCLE_SUPPRIME -> "Cycle supprimé";

            // UE
            case UE_CREEE -> "UE créée";
            case UE_MODIFIEE -> "UE modifiée";
            case UE_SUPPRIMEE -> "UE supprimée";

            // Classe
            case CLASSE_CREEE -> "Classe créée";
            case CLASSE_MODIFIEE -> "Classe modifiée";
            case CLASSE_SUPPRIMEE -> "Classe supprimée";

            // Inscription
            case INSCRIPTION_CREEE -> "Inscription créée";
            case INSCRIPTION_VALIDEE -> "Inscription validée";
            case INSCRIPTION_REFUSEE -> "Inscription refusée";
            case INSCRIPTION_SUPPRIMEE -> "Inscription supprimée";

            // Migration
            case MIGRATION_EFFECTUEE -> "Migration effectuée";

            // Séance
            case SEANCE_CREEE -> "Séance créée";
            case SEANCE_MODIFIEE -> "Séance modifiée";
            case SEANCE_SUPPRIMEE -> "Séance supprimée";

            // Programmation
            case PROGRAMMATION_CREEE -> "Programmation créée";
            case PROGRAMMATION_MODIFIEE -> "Programmation modifiée";
            case PROGRAMMATION_SUPPRIMEE -> "Programmation supprimée";

            // Appels
            case APPEL_LANCE -> "Appel lancé";
            case APPEL_CLOTURE -> "Appel clôturé";
            case PRESENCE_VALIDEE -> "Présence validée";
            case PRESENCE_REFUSEE -> "Présence refusée";
            case QR_CODE_GENERE -> "QR Code généré";
            case CODE_PIN_GENERE -> "Code PIN généré";
            case CODE_PIN_UTILISE -> "Code PIN utilisé";

            // Justificatif
            case JUSTIFICATIF_SOUMIS -> "Justificatif soumis";
            case JUSTIFICATIF_VALIDE -> "Justificatif validé";
            case JUSTIFICATIF_REFUSE -> "Justificatif refusé";
            case JUSTIFICATIF_SUPPRIME -> "Justificatif supprimé";

            // Notification
            case EMAIL_ENVOYE -> "Email envoyé";
            case SPECIALITE_SUPPRIMEE ->"Specialite supprimée";
            case NOTIFICATION_ENVOYEE -> "Notification envoyée";
            case SPECIALITE_CREEE -> "Specialitée crée";
            case SPECIALITE_MODIFIEE -> "Specialité modifiée";
            case NIVEAU_CREE -> "niveau crée";
            case NIVEAU_MODIFIE -> "niveau modifié";
            case NIVEAU_SUPPRIME -> "niveau supprimé";
        };
    }
}