package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Classe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Specialite;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.UE;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exception.DuplicateResourceException;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exception.ResourceNotFoundException;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Repository.ClassesRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Repository.UERepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple.InstitutSecurityService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UEService {

    private final UERepository ueRepo;
    private final SpecialiteService specialiteService;
    private final ClassesRepository classesRepo;
    private final InstitutSecurityService securityService;

    @Transactional
    public UE creer(UE ue, Long specialiteId) {
        Specialite specialite = specialiteService.findById(specialiteId);

        if (ueRepo.findByCode(ue.getCode()).isPresent()) {
            throw new DuplicateResourceException("UE", ue.getCode());
        }

        ue.setSpecialite(specialite);
        return ueRepo.save(ue);
    }

    @Transactional
    public UE modifier(Long id, UE data) {
        UE ue = findById(id);
        ue.setNom(data.getNom());
        ue.setCode(data.getCode());
        ue.setLibelle(data.getLibelle());
        ue.setLibelleAnglais(data.getLibelleAnglais());
        return ueRepo.save(ue);
    }

    public UE findById(Long id) {
        return ueRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UE", "id", id));
    }

    // ✅ Filtrage automatique selon le rôle
    public List<UE> getAll() {
        Long institutCible = securityService.getInstitutIdCourant();
        return institutCible != null ? ueRepo.findByInstitutIdWithDetails(institutCible) : ueRepo.findAll();
    }

    public List<UE> getBySpecialite(Long specialiteId) {
        return ueRepo.findBySpecialiteId(specialiteId);
    }

    public List<UE> getByClasse(Long classeId) {
        Classe classe = classesRepo.findById(classeId)
                .orElseThrow(() -> new ResourceNotFoundException("Classe", "id", classeId));
        Specialite specialite = classe.getNiveau() != null ? classe.getNiveau().getSpecialite() : null;
        if (specialite == null) return List.of();
        return ueRepo.findBySpecialiteId(specialite.getId());
    }

    public List<UE> getByAnnee(Long anneeId) {
        return ueRepo.findByAnneeAcademiqueId(anneeId);
    }

    public List<UE> rechercher(String nom) {
        return ueRepo.findByNomContainingIgnoreCase(nom);
    }

    @Transactional
    public void supprimer(Long id) {
        UE ue = findById(id);
        if (!ue.getProgrammations().isEmpty()) {
            throw new RuntimeException("Impossible de supprimer : cette UE est programmée dans " +
                    ue.getProgrammations().size() + " programme(s)");
        }
        ueRepo.delete(ue);
    }

    public List<UE> getByInstitut(Long institutId) {
        if (!securityService.canAccessInstitut(institutId)) {
            throw new AccessDeniedException("Accès refusé à cet institut");
        }
        return ueRepo.findByInstitutIdWithDetails(institutId);
    }
}