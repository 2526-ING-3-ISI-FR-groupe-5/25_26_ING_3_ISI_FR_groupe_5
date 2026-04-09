package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Cycle;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Ecole;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Filiere;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.filiere.FiliereRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exceptions.DuplicateResourceException;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Exceptions.ResourceNotFoundException;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.CycleRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.EcoleRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.FiliereRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FiliereService {

    private final FiliereRepository filiereRepository;
    private final EcoleRepository ecoleRepository;
    private final CycleRepository cycleRepository;

    @Transactional
    public Filiere creer(FiliereRequest request) {
        // Vérifier que l'école existe
        Ecole ecole = ecoleRepository.findById(request.getEcoleId())
                .orElseThrow(() -> new ResourceNotFoundException("École", "id", request.getEcoleId()));

        // Vérifier que le cycle existe
        Cycle cycle = cycleRepository.findById(request.getCycleId())
                .orElseThrow(() -> new ResourceNotFoundException("Cycle", "id", request.getCycleId()));

        // Vérifier si une filière avec le même code existe déjà dans la même école
        if (filiereRepository.existsByCodeAndEcoleId(request.getCode(), request.getEcoleId())) {
            throw new DuplicateResourceException("Filière", "code " + request.getCode() + " dans l'école " + ecole.getNom());
        }

        // Créer la filière
        Filiere filiere = new Filiere();
        filiere.setNom(request.getNom());
        filiere.setCode(request.getCode());
        filiere.setDescription(request.getDescription());
        filiere.setEcole(ecole);
        filiere.setCycle(cycle);

        return filiereRepository.save(filiere);
    }

    @Transactional
    public Filiere modifier(Long id, FiliereRequest request) {
        // Vérifier que la filière existe
        Filiere existante = findById(id);

        // Vérifier que l'école existe
        Ecole ecole = ecoleRepository.findById(request.getEcoleId())
                .orElseThrow(() -> new ResourceNotFoundException("École", "id", request.getEcoleId()));

        // Vérifier que le cycle existe
        Cycle cycle = cycleRepository.findById(request.getCycleId())
                .orElseThrow(() -> new ResourceNotFoundException("Cycle", "id", request.getCycleId()));

        // Vérifier si le nouveau code n'est pas déjà pris par une autre filière dans la même école
        if (!existante.getCode().equals(request.getCode()) &&
                filiereRepository.existsByCodeAndEcoleId(request.getCode(), request.getEcoleId())) {
            throw new DuplicateResourceException("Filière", "code " + request.getCode() + " dans l'école " + ecole.getNom());
        }

        // Mettre à jour
        existante.setNom(request.getNom());
        existante.setCode(request.getCode());
        existante.setDescription(request.getDescription());
        existante.setEcole(ecole);
        existante.setCycle(cycle);

        return filiereRepository.save(existante);
    }

    public Filiere findById(Long id) {
        return filiereRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Filière", "id", id));
    }

    public List<Filiere> getAll() {
        return filiereRepository.findAll();
    }

    public List<Filiere> getByEcole(Long ecoleId) {
        return filiereRepository.findByEcoleId(ecoleId);
    }

    public List<Filiere> getByCycle(Long cycleId) {
        return filiereRepository.findByCycleId(cycleId);
    }

    public List<Filiere> getByEcoleAndCycle(Long ecoleId, Long cycleId) {
        return filiereRepository.findByEcoleIdAndCycleId(ecoleId, cycleId);
    }

    public List<Filiere> searchByNom(String nom) {
        return filiereRepository.findByNomContainingIgnoreCase(nom);
    }

    public boolean existsByCodeAndEcole(String code, Long ecoleId) {
        return filiereRepository.existsByCodeAndEcoleId(code, ecoleId);
    }

    @Transactional
    public void supprimer(Long id) {
        Filiere filiere = findById(id);

        // Vérifier si la filière a des spécialités
        if (filiere.getSpecialites() != null && !filiere.getSpecialites().isEmpty()) {
            throw new RuntimeException("Impossible de supprimer la filière car elle contient " +
                    filiere.getSpecialites().size() + " spécialité(s). Supprimez d'abord les spécialités.");
        }

        // Vérifier si la filière a des niveaux
        if (filiere.getNiveaux() != null && !filiere.getNiveaux().isEmpty()) {
            throw new RuntimeException("Impossible de supprimer la filière car elle contient " +
                    filiere.getNiveaux().size() + " niveau(x). Supprimez d'abord les niveaux.");
        }

        filiereRepository.delete(filiere);
    }

    public long count() {
        return filiereRepository.count();
    }
}