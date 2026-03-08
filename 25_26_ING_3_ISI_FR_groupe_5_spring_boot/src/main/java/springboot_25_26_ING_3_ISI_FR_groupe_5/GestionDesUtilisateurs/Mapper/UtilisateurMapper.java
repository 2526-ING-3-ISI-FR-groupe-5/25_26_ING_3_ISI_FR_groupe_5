package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Response.UtilisateurResponseDTO;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.AssistantPedagogique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;

import java.util.List;

@Mapper(componentModel = "spring" , uses = {RoleMapper.class}, imports = {Enseignant.class, AssistantPedagogique.class})
public interface UtilisateurMapper {
    @Mapping(target = "dateNaissance", ignore = true)
    @Mapping(target = "type", expression = "java(utilisateur instanceof Enseignant ? \"ENS\" : \"ASP\")")
    @Mapping(target = "grade",  expression = "java(utilisateur instanceof Enseignant ? ((Enseignant) utilisateur).getGrade() : null)")
    @Mapping(target = "typeEnseignant",expression = "java(utilisateur instanceof Enseignant ? ((Enseignant) utilisateur).getTypeEnseignant() : null)")
    @Mapping(target = "fonction", expression = "java(utilisateur instanceof AssistantPedagogique  ? ((AssistantPedagogique)  utilisateur).getFonction() : null)")
    UtilisateurResponseDTO toDTO(Utilisateur utilisateur);
    default List<UtilisateurResponseDTO> toDTOList(List<Utilisateur> utilisateurs) {
        return utilisateurs.stream()
                .map(this::toDTO)
                .toList();
    }

}
