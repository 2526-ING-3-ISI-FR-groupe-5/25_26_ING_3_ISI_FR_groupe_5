package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Institut;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exceptions.DuplicateResourceException;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exceptions.ResourceNotFoundException;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.InstitutRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InstitutService {

    private final InstitutRepository institutRepository;

    @Transactional
    public Institut creer(Institut institut) {
        // Vérifier si un institut avec le même nom existe déjà
        if (institutRepository.existsByNomIgnoreCase(institut.getNom())) {
            throw new DuplicateResourceException("Institut", institut.getNom());
        }
        return institutRepository.save(institut);
    }

    @Transactional
    public Institut modifier(Long id, Institut institutModifie) {
        Institut existant = findById(id);

        // Vérifier si le nouveau nom n'est pas déjà pris par un autre institut
        if (!existant.getNom().equalsIgnoreCase(institutModifie.getNom()) &&
                institutRepository.existsByNomIgnoreCase(institutModifie.getNom())) {
            throw new DuplicateResourceException("Institut", institutModifie.getNom());
        }

        existant.setNom(institutModifie.getNom());
        existant.setVille(institutModifie.getVille());
        existant.setAdresse(institutModifie.getAdresse());
        existant.setEmail(institutModifie.getEmail());
        existant.setTelephone(institutModifie.getTelephone());
        existant.setLocalite(institutModifie.getLocalite());

        return institutRepository.save(existant);
    }

    public Institut findById(Long id) {
        return institutRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Institut " + id));
    }

    public List<Institut> getAll() {
        return institutRepository.findAll(Sort.by("nom").ascending());
    }

    public Page<Institut> getAllPaginated(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("nom").ascending());
        if (search != null && !search.isEmpty()) {
            return institutRepository.search(search, pageable);
        }
        return institutRepository.findAll(pageable);
    }

    public List<Institut> getByVille(String ville) {
        return institutRepository.findByVilleContainingIgnoreCase(ville);
    }

    @Transactional
    public void supprimer(Long id) {
        Institut institut = findById(id);

        // Vérifier si l'institut a des écoles
        if (institut.getEcoles() != null && !institut.getEcoles().isEmpty()) {
            throw new RuntimeException("Impossible de supprimer l'institut car il contient " +
                    institut.getEcoles().size() + " école(s). Supprimez d'abord les écoles.");
        }

        institutRepository.delete(institut);
    }

    public long count() {
        return institutRepository.count();
    }
}