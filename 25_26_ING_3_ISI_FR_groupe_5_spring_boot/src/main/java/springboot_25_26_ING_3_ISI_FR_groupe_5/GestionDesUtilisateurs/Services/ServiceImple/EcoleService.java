package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Ecole;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Institut;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.ecole.EcoleRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exceptions.DuplicateResourceException;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exceptions.ResourceNotFoundException;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers.EcoleMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.EcoleRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EcoleService {

    private final EcoleRepository ecoleRepository;
    private final InstitutService institutService;
    private final EcoleMapper ecoleMapper;

    @Transactional
    public Ecole creer(EcoleRequest request) {
        // 1. Vérifier que l'institut existe
        Institut institut = institutService.findById(request.getInstitutId());

        // 2. Vérifier si une école avec le même nom existe déjà dans cet institut
        if (ecoleRepository.existsByNomAndInstitutId(request.getNom(), request.getInstitutId())) {
            throw new DuplicateResourceException("École", request.getNom() + " dans l'institut " + institut.getNom());
        }

        // 3. Créer l'école
        Ecole ecole = ecoleMapper.toEntity(request);
        ecole.setInstitut(institut);

        return ecoleRepository.save(ecole);
    }

    @Transactional
    public Ecole modifier(Long id, EcoleRequest request) {
        // 1. Vérifier que l'école existe
        Ecole existante = findById(id);

        // 2. Vérifier que l'institut existe
        Institut institut = institutService.findById(request.getInstitutId());

        // 3. Vérifier si le nouveau nom n'est pas déjà pris (si changé)
        if (!existante.getNom().equalsIgnoreCase(request.getNom()) &&
                ecoleRepository.existsByNomAndInstitutId(request.getNom(), request.getInstitutId())) {
            throw new DuplicateResourceException("École", request.getNom() + " dans l'institut " + institut.getNom());
        }

        // 4. Mettre à jour
        existante.setNom(request.getNom());
        existante.setAdresse(request.getAdresse());
        existante.setEmail(request.getEmail());
        existante.setTelephone(request.getTelephone());
        existante.setInstitut(institut);

        return ecoleRepository.save(existante);
    }

    public Ecole findById(Long id) {
        return ecoleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("École"+ "id" + id));
    }

    public List<Ecole> getAll() {
        return ecoleRepository.findAll(Sort.by("nom").ascending());
    }

    public Page<Ecole> getAllPaginated(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("nom").ascending());
        if (search != null && !search.isEmpty()) {
            return ecoleRepository.search(search, pageable);
        }
        return ecoleRepository.findAll(pageable);
    }

    public List<Ecole> getByInstitut(Long institutId) {
        return ecoleRepository.findByInstitutId(institutId);
    }

    public Page<Ecole> getByInstitutPaginated(Long institutId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("nom").ascending());
        return ecoleRepository.findByInstitutId(institutId, pageable);
    }

    @Transactional
    public void supprimer(Long id) {
        Ecole ecole = findById(id);

        // Vérifier si l'école a des filières
        if (ecole.getFilieres() != null && !ecole.getFilieres().isEmpty()) {
            throw new RuntimeException("Impossible de supprimer l'école car elle contient " +
                    ecole.getFilieres().size() + " filière(s). Supprimez d'abord les filières.");
        }

        ecoleRepository.delete(ecole);
    }

    public long count() {
        return ecoleRepository.count();
    }
}