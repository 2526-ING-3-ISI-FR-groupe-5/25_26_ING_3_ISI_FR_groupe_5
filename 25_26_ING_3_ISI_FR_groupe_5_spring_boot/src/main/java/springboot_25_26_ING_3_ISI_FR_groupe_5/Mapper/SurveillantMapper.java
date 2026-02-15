package springboot_25_26_ING_3_ISI_FR_groupe_5.Mapper;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import springboot_25_26_ING_3_ISI_FR_groupe_5.DTO.SurveillantDTO;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entites.Surveillant;

import java.util.stream.Collectors;

@Component
public class SurveillantMapper extends UtilisateurMapper<Surveillant, SurveillantDTO> {

    @Override
    protected String getDiscriminatorValue() {
        return "SRV";
    }

    @Override
    public SurveillantDTO toDTO(Surveillant entity) {
        if (entity == null) return null;

        SurveillantDTO dto = new SurveillantDTO();

        // Copie des propriétés communes
        copyCommonPropertiesToDTO(entity, dto);

        // Copie des propriétés spécifiques
        BeanUtils.copyProperties(entity, dto,
                "classes", "password", "otpCode", "otpExpiration");



        return dto;
    }

    @Override
    public Surveillant toEntity(SurveillantDTO dto) {
        if (dto == null) return null;

        Surveillant entity = new Surveillant();

        // Copie des propriétés communes
        copyCommonPropertiesToEntity(dto, entity);

        // Copie des propriétés spécifiques
        BeanUtils.copyProperties(dto, entity,
                "classeIds", "id", "password");

        return entity;
    }

    @Override
    public void updateEntityFromDTO(SurveillantDTO dto, Surveillant entity) {
        if (dto == null || entity == null) return;

        // Mise à jour des propriétés communes
        updateCommonProperties(dto, entity);

        // Mise à jour des propriétés spécifiques
        BeanUtils.copyProperties(dto, entity,
                "id", "classeIds", "password", "dateCreation");
    }
}