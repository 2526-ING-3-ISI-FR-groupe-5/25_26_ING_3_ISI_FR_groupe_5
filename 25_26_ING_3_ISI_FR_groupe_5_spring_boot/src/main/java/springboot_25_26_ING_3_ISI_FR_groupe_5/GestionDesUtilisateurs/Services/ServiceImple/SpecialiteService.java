package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Specialite;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exceptions.DuplicateResourceException;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exceptions.ResourceNotFoundException;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.SpecialiteRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecialiteService {

    private final SpecialiteRepository specialiteRepository;

    @Transactional
    public Specialite creer(Specialite specialite) {
        if (specialite.getCode() == null || specialiteRepository.existsByCode(specialite.getCode())) {
            throw new DuplicateResourceException("Spécialité", specialite.getCode());
        }
        return specialiteRepository.save(specialite);
    }

    @Transactional
    public Specialite modifier(Long id, Specialite data) {
        Specialite specialite = findById(id);

        // Vérifier si le nouveau code n'est pas déjà pris
        if (!specialite.getCode().equals(data.getCode()) &&
                specialiteRepository.existsByCode(data.getCode())) {
            throw new DuplicateResourceException("Spécialité", data.getCode());
        }

        specialite.setNom(data.getNom());
        specialite.setCode(data.getCode());
        specialite.setDescription(data.getDescription());

        return specialiteRepository.save(specialite);
    }

    public Specialite findById(Long id) {
        return specialiteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Spécialité", "id", id));
    }

    public List<Specialite> getAll() {
        return specialiteRepository.findAll();
    }

    public List<Specialite> getByFiliere(Long filiereId) {
        return specialiteRepository.findByFiliereId(filiereId);
    }

    @Transactional
    public void supprimer(Long id) {
        Specialite specialite = findById(id);

        // Vérifier si la spécialité a des niveaux
        if (specialite.getNiveaux() != null && !specialite.getNiveaux().isEmpty()) {
            throw new RuntimeException("Impossible de supprimer la spécialité car elle contient " +
                    specialite.getNiveaux().size() + " niveau(x). Supprimez d'abord les niveaux.");
        }

        specialiteRepository.delete(specialite);
    }
}