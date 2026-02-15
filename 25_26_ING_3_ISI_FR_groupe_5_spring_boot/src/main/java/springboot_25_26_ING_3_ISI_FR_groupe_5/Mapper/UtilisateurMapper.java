package springboot_25_26_ING_3_ISI_FR_groupe_5.Mapper;

import org.springframework.beans.BeanUtils;
import springboot_25_26_ING_3_ISI_FR_groupe_5.DTO.AssistantPedagogiqueDTO;
import springboot_25_26_ING_3_ISI_FR_groupe_5.DTO.UtilisateurDTO;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entites.AssistantPedagogique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entites.Utilisateur;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entites.Role;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entites.Institut;

import java.util.stream.Collectors;

/**
 * Mapper abstrait pour gérer les propriétés communes des utilisateurs
 */
public abstract class UtilisateurMapper<E extends Utilisateur, D extends UtilisateurDTO> {

    /**
     * Copie les propriétés communes de l'entité vers le DTO
     */
    protected void copyCommonPropertiesToDTO(E entity, D dto) {
        if (entity == null || dto == null) {
            return;
        }

        // Copie des propriétés simples communes
        BeanUtils.copyProperties(entity, dto,
                "password", "otpCode", "otpExpiration", // Propriétés sensibles à exclure
                "roles", "institutCollection", "localisations", "localisation", "admin"
        );

        // Mapping du statut
        dto.setStatus(determineStatus(entity));

        // Mapping du type (discriminateur)
        dto.setType(getDiscriminatorValue());

        // Mapping des relations communes
        if (entity.getLocalisation() != null) {
            dto.setLocalisationId(entity.getLocalisation().getId());
        }

        if (entity.getRoles() != null) {
            dto.setRoleIds(
                    entity.getRoles().stream()
                            .map(Role::getId)
                            .collect(Collectors.toList())
            );
        }

        if (entity.getInstitutCollection() != null) {
            dto.setInscriptionIds(
                    entity.getInstitutCollection().stream()
                            .map(Institut::getId)
                            .collect(Collectors.toList())
            );
        }
    }

    /**
     * Copie les propriétés communes du DTO vers l'entité
     */
    protected void copyCommonPropertiesToEntity(D dto, E entity) {
        if (dto == null || entity == null) {
            return;
        }

        // Copie des propriétés simples communes
        BeanUtils.copyProperties(dto, entity,
                "id", "password", // ID et password ne doivent pas être copiés de cette façon
                "roleIds", "institutIds", "localisationId", // Relations à gérer séparément
                "status", "type" // Propriétés calculées ou discriminateurs
        );
    }

    /**
     * Met à jour une entité existante avec les données du DTO (propriétés communes)
     */
    protected void updateCommonProperties(D dto, E entity) {
        if (dto == null || entity == null) {
            return;
        }

        // Copie des propriétés simples communes (exclut l'ID)
        BeanUtils.copyProperties(dto, entity,
                "id", "password", "dateCreation", // Ne pas mettre à jour ces champs
                "roleIds", "institutIds", "localisationId", // Relations à gérer séparément
                "roles", "institutCollection", "localisation", "admin", "localisations",
                "status", "type", "otpCode", "otpExpiration"
        );
    }

    /**
     * Détermine le statut de l'utilisateur basé sur ses propriétés
     */
    private String determineStatus(E entity) {
        if (!entity.isEnabled()) {
            return "Non activé";
        }
        if (entity.isAccountLocked()) {
            return "Bloqué";
        }
        if (!entity.getActive()) {
            return "Inactif";
        }
        return "Actif";
    }

    /**
     * Retourne la valeur du discriminateur pour ce type d'utilisateur
     * À implémenter dans les classes concrètes
     */
    protected abstract String getDiscriminatorValue();

    /**
     * Convertit une entité en DTO
     * À implémenter dans les classes concrètes
     */
    public abstract D toDTO(E entity);

    /**
     * Convertit un DTO en entité
     * À implémenter dans les classes concrètes
     */
    public abstract E toEntity(D dto);

    /**
     * Met à jour une entité existante avec les données d'un DTO
     * À implémenter dans les classes concrètes
     */
    public abstract void updateEntityFromDTO(D dto, E entity);

    public abstract AssistantPedagogique toEntity(AssistantPedagogiqueDTO dto);

    public abstract void updateEntityFromDTO(AssistantPedagogiqueDTO dto, AssistantPedagogique entity);
}