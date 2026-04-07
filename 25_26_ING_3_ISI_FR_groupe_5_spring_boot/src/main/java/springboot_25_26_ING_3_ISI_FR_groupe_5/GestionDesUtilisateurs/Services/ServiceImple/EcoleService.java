package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Ecole;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository.EcoleRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EcoleService {

    private final EcoleRepository ecoleRepo;

    @Transactional
    public Ecole creer(Ecole ecole) {
        if (ecoleRepo.existsByNom(ecole.getNom())) {
            throw new RuntimeException("Une école avec ce nom existe déjà");
        }
        if (ecoleRepo.existsByEmail(ecole.getEmail())) {
            throw new RuntimeException("Une école avec cet email existe déjà");
        }
        return ecoleRepo.save(ecole);
    }

    @Transactional
    public Ecole modifier(Long id, Ecole data) {
        Ecole ecole = findById(id);
        ecole.setNom(data.getNom());
        ecole.setAdresse(data.getAdresse());
        ecole.setEmail(data.getEmail());
        ecole.setTelephone(data.getTelephone());
        return ecoleRepo.save(ecole);
    }

    public Ecole findById(Long id) {
        return ecoleRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("École introuvable"));
    }

    public List<Ecole> getAll() {
        return ecoleRepo.findAll();
    }

    @Transactional
    public void supprimer(Long id) {
        ecoleRepo.delete(findById(id));
    }
}
