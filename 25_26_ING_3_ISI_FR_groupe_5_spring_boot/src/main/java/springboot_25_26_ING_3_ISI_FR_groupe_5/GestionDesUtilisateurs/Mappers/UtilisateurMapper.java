package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.utilisateur.UtilisateurResponse;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.AssistantPedagogique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Surveillant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {RoleMapper.class},
        imports = {Enseignant.class, AssistantPedagogique.class, Surveillant.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UtilisateurMapper {

    // ✅ Champ type — discriminator lisible côté Thymeleaf
    @Mapping(target = "type", expression = """
            java(utilisateur instanceof Enseignant  ? "ENS" :
                 utilisateur instanceof AssistantPedagogique   ? "AST" :
                 utilisateur instanceof Surveillant ? "SUR" : "INCONNU")
            """)
    @Mapping(target = "grade",
            expression = "java(utilisateur instanceof Enseignant ? ((Enseignant) utilisateur).getGrade() : null)")
    @Mapping(target = "typeEnseignant",
            expression = "java(utilisateur instanceof Enseignant ? ((Enseignant) utilisateur).getTypeEnseignant() : null)")
    @Mapping(target = "fonction",
            expression = "java(utilisateur instanceof AssistantPedagogique ? ((AssistantPedagogique) utilisateur).getFonction() : null)")
    @Mapping(target = "secteur",
            expression = "java(utilisateur instanceof Surveillant ? ((Surveillant) utilisateur).getSecteur() : null)")
    @Mapping(target = "typeContrat",
            expression = "java(utilisateur instanceof Surveillant ? ((Surveillant) utilisateur).getTypeContrat().name() : null)")
    UtilisateurResponse toDTO(Utilisateur utilisateur);

    default List<UtilisateurResponse> toDTOList(List<Utilisateur> utilisateurs) {
        return utilisateurs.stream().map(this::toDTO).toList();
    }

}
