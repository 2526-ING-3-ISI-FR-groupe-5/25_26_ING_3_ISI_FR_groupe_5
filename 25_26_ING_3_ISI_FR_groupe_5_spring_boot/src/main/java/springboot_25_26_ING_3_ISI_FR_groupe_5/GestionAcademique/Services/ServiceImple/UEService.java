package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Classe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Specialite;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.UE;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exception.DuplicateResourceException;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exception.ResourceNotFoundException;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Repository.ClassesRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Repository.UERepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UEService {

    private final UERepository ueRepo;
    private final SpecialiteService specialiteService;
    private final ClassesRepository classesRepo;

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

    public List<UE> getAll() {
        return ueRepo.findAllUes();
    }

    // ✅ Méthode ajoutée pour résoudre l'erreur
    public List<UE> getByAnnee(Long anneeId) {
        // Retourne toutes les UE (indépendamment de l'année)
        // Tu peux adapter selon ton besoin
        return ueRepo.findAllUes();
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

    public List<UE> getByNiveau(Long id) {
        return null;
    }
}