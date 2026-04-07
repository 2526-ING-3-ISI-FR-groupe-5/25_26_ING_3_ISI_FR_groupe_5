package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Specialite;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.UE;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.UERepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UEService {

    private final UERepository ueRepo;
    private final SpecialiteService specialiteService;
    private final AnneeAcademiqueService anneeService;

    @Transactional
    public UE creer(UE ue, Long specialiteId) {
        Specialite specialite = specialiteService.findById(specialiteId);

        if (ueRepo.findByCode(ue.getCode()).isPresent()) {
            throw new RuntimeException("Une UE avec ce code existe déjà");
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
                .orElseThrow(() -> new RuntimeException("UE introuvable"));
    }

    // ✅ CORRECTION : Retourner TOUTES les UE
    public List<UE> getByAnnee(Long anneeId) {
        // Vérifier que l'année existe (optionnel)
        if (anneeId != null) {
            try {
                anneeService.findById(anneeId);
            } catch (Exception e) {
                // Année non trouvée
            }
        }
        // Retourner TOUTES les UE
        return ueRepo.findAllUes();
    }

    public List<UE> getByClasseAndAnnee(Long classeId, Long anneeId) {
        return ueRepo.findByClasseAndAnnee(classeId, anneeId);
    }

    public List<UE> rechercher(String nom) {
        return ueRepo.findByNomContainingIgnoreCase(nom);
    }

    @Transactional
    public void supprimer(Long id) {
        UE ue = findById(id);
        if (!ue.getProgrammations().isEmpty()) {
            throw new RuntimeException(
                    "Impossible de supprimer : cette UE est programmée dans une ou plusieurs années"
            );
        }
        ueRepo.delete(ue);
    }

    public List<UE> getAll() {
        return ueRepo.findAllUes();
    }

    public List<UE> getBySpecialite(Long specialiteId) {
        return ueRepo.findBySpecialiteId(specialiteId);
    }
}