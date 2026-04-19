package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Ecole;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Institut;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.ecole.EcoleRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.TypeAction;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exceptions.DuplicateResourceException;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exceptions.ResourceNotFoundException;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers.EcoleMapper;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.EcoleRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService.IEcoleService;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.InterfaceService.IJournalActionService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EcoleService implements IEcoleService {

    private final EcoleRepository ecoleRepository;
    private final InstitutService institutService;
    private final EcoleMapper ecoleMapper;
    private final IJournalActionService journalService;

    @Override
    @Transactional
    public Ecole creer(EcoleRequest request, Utilisateur acteur) {
        try {
            Institut institut = institutService.findById(request.getInstitutId());

            if (ecoleRepository.existsByNomAndInstitutId(request.getNom(), request.getInstitutId())) {
                throw new DuplicateResourceException("École", request.getNom() + " dans l'institut " + institut.getNom());
            }

            Ecole ecole = ecoleMapper.toEntity(request);
            ecole.setInstitut(institut);

            Ecole saved = ecoleRepository.save(ecole);

            // ✅ Journalisation
            journalService.journaliserCreationEcole(acteur, saved.getId(), saved.getNom());

            return saved;

        } catch (DuplicateResourceException e) {
            journalService.journaliserEchec(acteur, TypeAction.ECOLE_CREEE,
                    "Ecole", null, e.getMessage());
            throw e;
        } catch (Exception e) {
            journalService.journaliserEchec(acteur, TypeAction.ECOLE_CREEE,
                    "Ecole", null, e.getMessage());
            throw new RuntimeException("Erreur lors de la création de l'école", e);
        }
    }

    @Override
    @Transactional
    public Ecole modifier(Long id, EcoleRequest request, Utilisateur acteur) {
        try {
            Ecole existante = findById(id);
            Institut institut = institutService.findById(request.getInstitutId());

            if (!existante.getNom().equalsIgnoreCase(request.getNom()) &&
                    ecoleRepository.existsByNomAndInstitutId(request.getNom(), request.getInstitutId())) {
                throw new DuplicateResourceException("École", request.getNom() + " dans l'institut " + institut.getNom());
            }

            existante.setNom(request.getNom());
            existante.setAdresse(request.getAdresse());
            existante.setEmail(request.getEmail());
            existante.setTelephone(request.getTelephone());
            existante.setInstitut(institut);

            Ecole saved = ecoleRepository.save(existante);

            // ✅ Journalisation
            journalService.journaliserModificationEcole(acteur, saved.getId(), saved.getNom());

            return saved;

        } catch (DuplicateResourceException e) {
            journalService.journaliserEchec(acteur, TypeAction.ECOLE_MODIFIEE,
                    "Ecole", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            journalService.journaliserEchec(acteur, TypeAction.ECOLE_MODIFIEE,
                    "Ecole", id, e.getMessage());
            throw new RuntimeException("Erreur lors de la modification de l'école", e);
        }
    }

    @Override
    public Ecole findById(Long id) {
        return ecoleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("École", "id", id));
    }

    @Override
    public List<Ecole> getAll() {
        return ecoleRepository.findAll(Sort.by("nom").ascending());
    }

    @Override
    public Page<Ecole> getAllPaginated(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("nom").ascending());
        if (search != null && !search.isEmpty()) {
            return ecoleRepository.search(search, pageable);
        }
        return ecoleRepository.findAll(pageable);
    }

    @Override
    public List<Ecole> getByInstitut(Long institutId) {
        return ecoleRepository.findByInstitutId(institutId);
    }

    @Override
    public Page<Ecole> getByInstitutPaginated(Long institutId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("nom").ascending());
        return ecoleRepository.findByInstitutId(institutId, pageable);
    }

    @Override
    @Transactional
    public void supprimer(Long id, Utilisateur acteur) {
        try {
            Ecole ecole = findById(id);

            if (ecole.getFilieres() != null && !ecole.getFilieres().isEmpty()) {
                throw new RuntimeException("Impossible de supprimer l'école car elle contient " +
                        ecole.getFilieres().size() + " filière(s). Supprimez d'abord les filières.");
            }

            ecoleRepository.delete(ecole);

            // ✅ Journalisation
            journalService.journaliserSuppressionEcole(acteur, id, ecole.getNom());

        } catch (RuntimeException e) {
            journalService.journaliserEchec(acteur, TypeAction.ECOLE_SUPPRIMEE,
                    "Ecole", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            journalService.journaliserEchec(acteur, TypeAction.ECOLE_SUPPRIMEE,
                    "Ecole", id, e.getMessage());
            throw new RuntimeException("Erreur lors de la suppression de l'école", e);
        }
    }

    @Override
    public long count() {
        return ecoleRepository.count();
    }
}