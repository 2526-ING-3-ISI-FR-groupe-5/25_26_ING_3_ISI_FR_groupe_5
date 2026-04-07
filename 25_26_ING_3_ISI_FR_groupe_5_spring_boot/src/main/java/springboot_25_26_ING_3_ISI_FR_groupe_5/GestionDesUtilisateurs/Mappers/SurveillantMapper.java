package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers;

import org.mapstruct.Mapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.surveillant.SurveillantRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.surveillant.SurveillantResponse;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Surveillant;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SurveillantMapper {

    Surveillant toEntity(SurveillantRequest request);
    SurveillantResponse toResponse(Surveillant surveillant);
    List<SurveillantResponse> toResponseList(List<Surveillant> surveillants);
}