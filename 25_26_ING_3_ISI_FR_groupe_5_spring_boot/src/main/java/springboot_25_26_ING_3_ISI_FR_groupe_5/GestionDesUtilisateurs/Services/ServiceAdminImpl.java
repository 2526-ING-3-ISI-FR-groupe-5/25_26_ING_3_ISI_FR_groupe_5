package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Response.AssistantResponseDetails;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Response.EnseignantResponseDetails;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Response.UtilisateurResponseDTO;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.AssistantPedagogique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exceptions.UserNotFoundException;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mapper.AssistantMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mapper.EnseignantMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mapper.UtilisateurMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.AssistantPedagogiqueRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.EnseignantRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.UtilisateurRepository;

@Service
@RequiredArgsConstructor
public class ServiceAdminImpl implements InterfaceAdminService {
    private  final UtilisateurRepository utilisateurRepository;
    AssistantPedagogiqueRepository aPedagogiqueRepository;
    private  final UtilisateurMapper  utilisateurMapper;
    private final EnseignantMapper  enseignantMapper;
    private  final EnseignantRepository enseignantRepository;
    private  final AssistantPedagogiqueRepository  assistantPedagogiqueRepository;
    private final AssistantMapper assistantMapper;



    @Transactional
    @Override
    public Page<UtilisateurResponseDTO> listeTous(String recherche, String type, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("nom").ascending());
        return utilisateurRepository.searchWithFilters(recherche, type, pageable).map(utilisateurMapper::toDTO);
    }
@Transactional
@Override
public void deleteUtilisateur(Long id){
        if (utilisateurRepository.existsById(id)){
            utilisateurRepository.deleteById(id);
        }else {
            throw new UserNotFoundException("Utilisateur n'existe pas" );
        }

    }
@Transactional
@Override
public EnseignantResponseDetails getEnseignant(Long id){
    Enseignant enseignant= enseignantRepository.findById(id)
            .orElseThrow(()-> new UserNotFoundException("Utilisateur n'existe pas"));

    return enseignantMapper.toDTOdetails(enseignant);

    }

    public AssistantResponseDetails getAssistant(Long id){
        AssistantPedagogique assistantPedagogique= assistantPedagogiqueRepository
                .findById(id).orElseThrow(()-> new UserNotFoundException("Assistant n'existe pas"));

        return assistantMapper.toDTOdetails(assistantPedagogique);
    }
    @Transactional
    @Override
    public EnseignantResponseDetails EnsDetails(Long id) {
        Enseignant enseignant = enseignantRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur inexistant avec l'id : " + id));
        return enseignantMapper.toDTOdetails(enseignant);
    }
    @Transactional
    @Override
    public AssistantResponseDetails AssDetails(Long id){
        AssistantPedagogique assistant= assistantPedagogiqueRepository.findById(id).orElseThrow(()-> new UsernameNotFoundException("Utilisateur inexistant avec l'id : " + id));

        return assistantMapper.toDTOdetails(assistant);
    }
}
