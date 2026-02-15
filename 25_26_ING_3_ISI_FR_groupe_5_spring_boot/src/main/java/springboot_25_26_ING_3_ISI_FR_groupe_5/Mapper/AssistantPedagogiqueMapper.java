package springboot_25_26_ING_3_ISI_FR_groupe_5.Mapper;


import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import springboot_25_26_ING_3_ISI_FR_groupe_5.DTO.AssistantPedagogiqueDTO;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entites.AssistantPedagogique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entites.Appels;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entites.Justificatif;

import java.util.stream.Collectors;

@Component
public class AssistantPedagogiqueMapper extends UtilisateurMapper<AssistantPedagogique, AssistantPedagogiqueDTO> {

    @Override
    protected String getDiscriminatorValue() {
        return "ASST_PEDA"; // Valeur adaptée à ton modèle
    }

    @Override
    public AssistantPedagogiqueDTO toDTO(AssistantPedagogique entity) {
        if (entity == null) return null;

        AssistantPedagogiqueDTO dto = new AssistantPedagogiqueDTO();

        // Copie des propriétés communes
        copyCommonPropertiesToDTO(entity, dto);

        // Copie des propriétés spécifiques
        BeanUtils.copyProperties(entity, dto, "ecoles", "appels", "justificatif");

        // Mappage des relations
        if (entity.getEcoles() != null) {
            dto.setEcoleId(entity.getEcoles().getId());
        }
        if (entity.getAppels() != null) {
            dto.setAppelsIds(entity.getAppels().stream()
                    .map(Appels::getId)
                    .collect(Collectors.toList()));
        }
        if (entity.getJustificatif() != null) {
            dto.setJustificatifsIds(entity.getJustificatif().stream()
                    .map(Justificatif::getId)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    @Override
    public AssistantPedagogique toEntity(AssistantPedagogiqueDTO dto) {
        if (dto == null) return null;

        AssistantPedagogique entity = new AssistantPedagogique();

        // Copie des propriétés communes
        copyCommonPropertiesToEntity(dto, entity);

        // Copie des propriétés spécifiques
        BeanUtils.copyProperties(dto, entity, "ecoleId", "appelsIds", "justificatifsIds", "id", "password");

        // Mappage des relations (si nécessaire, selon ton contexte)
        // Exemple : entity.setEcoles(ecoleRepository.findById(dto.getEcoleId()).orElse(null));

        return entity;
    }

    @Override
    public void updateEntityFromDTO(AssistantPedagogiqueDTO dto, AssistantPedagogique entity) {
        if (dto == null || entity == null) return;

        // Mise à jour des propriétés communes
        updateCommonProperties(dto, entity);

        // Mise à jour des propriétés spécifiques
        BeanUtils.copyProperties(dto, entity,
                "id", "ecoleId", "appelsIds", "justificatifsIds", "password", "dateCreation");

        // Mise à jour des relations (si nécessaire)
        // Exemple : entity.setEcoles(ecoleRepository.findById(dto.getEcoleId()).orElse(null));
    }
}
