package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mapper;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses={RoleMapper.class})
public interface EtudiantMapper {

}
