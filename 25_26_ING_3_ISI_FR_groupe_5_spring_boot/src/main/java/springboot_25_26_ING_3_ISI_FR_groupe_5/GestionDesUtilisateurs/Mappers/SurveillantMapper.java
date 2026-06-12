package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers;

import org.mapstruct.Mapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.surveillant.SurveillantResponse;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.surveillant.SurveillantRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.utilisateur.SurveillantResponseDetails;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Surveillant;

import java.util.List;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface SurveillantMapper {
    SurveillantResponseDetails toDtoDetails(Surveillant surveillant);

    SurveillantResponse toResponse(Surveillant surveillant);

    Surveillant toEntity(SurveillantRequest request);

    List<SurveillantResponse> toResponseList(List<Surveillant> surveillants);
}