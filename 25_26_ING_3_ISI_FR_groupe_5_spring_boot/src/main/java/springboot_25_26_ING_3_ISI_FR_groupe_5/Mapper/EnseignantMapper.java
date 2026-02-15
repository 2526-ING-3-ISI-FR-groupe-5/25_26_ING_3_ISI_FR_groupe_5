package springboot_25_26_ING_3_ISI_FR_groupe_5.Mapper;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import springboot_25_26_ING_3_ISI_FR_groupe_5.DTO.EnseignantDTO;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entites.Enseignant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entites.ValidationPresence;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entites.SeanceCours;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entites.Appels;

import java.util.stream.Collectors;

@Component
public class EnseignantMapper extends UtilisateurMapper<Enseignant, EnseignantDTO> {

    @Override
    protected String getDiscriminatorValue() {
        return "ENS";
    }

    @Override
    public EnseignantDTO toDTO(Enseignant enseignant) {
        if (enseignant == null) {
            return null;
        }

        EnseignantDTO enseignantDTO = new EnseignantDTO();

        // Copie des propriétés communes via la classe parent
        copyCommonPropertiesToDTO(enseignant, enseignantDTO);

        // Copie des propriétés spécifiques à Enseignant
        BeanUtils.copyProperties(enseignant, enseignantDTO,
                "validationPresences", "seancecours", "appels"
        );

        // Mapping des relations spécifiques
        if (enseignant.getValidationPresences() != null) {
            enseignantDTO.setValidationPresenceIds(
                    enseignant.getValidationPresences().stream()
                            .map(ValidationPresence::getId)
                            .collect(Collectors.toList())
            );
        }

        if (enseignant.getSeancecours() != null) {
            enseignantDTO.setSeanceCoursIds(
                    enseignant.getSeancecours().stream()
                            .map(SeanceCours::getId)
                            .collect(Collectors.toList())
            );
        }

        if (enseignant.getAppels() != null) {
            enseignantDTO.setAppelIds(
                    enseignant.getAppels().stream()
                            .map(Appels::getId)
                            .collect(Collectors.toList())
            );
        }

        return enseignantDTO;
    }

    @Override
    public Enseignant toEntity(EnseignantDTO enseignantDTO) {
        if (enseignantDTO == null) {
            return null;
        }

        Enseignant enseignant = new Enseignant();

        // Copie des propriétés communes via la classe parent
        copyCommonPropertiesToEntity(enseignantDTO, enseignant);

        // Copie des propriétés spécifiques à Enseignant
        BeanUtils.copyProperties(enseignantDTO, enseignant,
                "validationPresenceIds", "seanceCoursIds", "appelIds"
        );

        // Les relations doivent être gérées par le service

        return enseignant;
    }

    @Override
    public void updateEntityFromDTO(EnseignantDTO enseignantDTO, Enseignant enseignant) {
        if (enseignantDTO == null || enseignant == null) {
            return;
        }

        // Mise à jour des propriétés communes via la classe parent
        updateCommonProperties(enseignantDTO, enseignant);

        // Mise à jour des propriétés spécifiques à Enseignant
        BeanUtils.copyProperties(enseignantDTO, enseignant,
                "id", "dateCreation", "password",
                "validationPresenceIds", "seanceCoursIds", "appelIds",
                "validationPresences", "seancecours", "appels"
        );

        // Les relations doivent être gérées par le service
    }
}