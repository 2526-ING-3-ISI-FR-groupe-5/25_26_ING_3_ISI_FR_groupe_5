package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Export.EtudiantExportDTO;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Export.UtilisateurExportDTO;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.*;

import java.util.List;

@Mapper(componentModel = "spring", imports = {Enseignant.class, AssistantPedagogique.class, Surveillant.class})
public interface ExportMapper {

    // ══════════════════════════════════════════
    // UTILISATEUR → DTO EXPORT
    // ══════════════════════════════════════════

    @Mapping(target = "type", expression = """
            java(u instanceof Enseignant  ? "ENS" :
                 u instanceof AssistantPedagogique   ? "AST" :
                 u instanceof Surveillant ? "SUR" : "INCONNU")
            """)
    @Mapping(target = "grade", expression = "java(u instanceof Enseignant ? ((Enseignant) u).getGrade() : null)")
    @Mapping(target = "fonction", expression = "java(u instanceof AssistantPedagogique ? ((AssistantPedagogique) u).getFonction() : null)")
    @Mapping(target = "secteur", expression = "java(u instanceof Surveillant ? ((Surveillant) u).getSecteur() : null)")
    @Mapping(target = "institutNom", source = "institut.nom")
    UtilisateurExportDTO toExportDTO(Utilisateur u);

    List<UtilisateurExportDTO> toExportDTOList(List<Utilisateur> utilisateurs);

    // ══════════════════════════════════════════
    // ÉTUDIANT → DTO EXPORT
    // ══════════════════════════════════════════

    @Mapping(target = "matricule", source = "matricule")
    @Mapping(target = "nom", source = "nom")
    @Mapping(target = "prenom", source = "prenom")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "telephone", source = "telephone")
    @Mapping(target = "classeNom", expression = "java(e.getClasse() != null ? e.getClasse().getNom() : null)")
    @Mapping(target = "niveauNom", expression = "java(e.getClasse() != null && e.getClasse().getNiveau() != null ? e.getClasse().getNiveau().getNom() : null)")
    @Mapping(target = "filiereNom", expression = "java(e.getFiliereNom())")
    @Mapping(target = "anneeAcademique", ignore = true)
    @Mapping(target = "semestre", ignore = true)
    @Mapping(target = "statutInscription", ignore = true)
    @Mapping(target = "decisionFinAnnee", ignore = true)
    @Mapping(target = "active", source = "active")
    EtudiantExportDTO toExportDTO(Etudiant e);

    List<EtudiantExportDTO> toEtudiantExportDTOList(List<Etudiant> etudiants);
}